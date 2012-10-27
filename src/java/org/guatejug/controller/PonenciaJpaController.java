/*
 *    Copyright (C) 2012 Victor Orozco
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.

 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.guatejug.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.guatejug.data.Ponente;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import org.guatejug.controller.exceptions.IllegalOrphanException;
import org.guatejug.controller.exceptions.NonexistentEntityException;
import org.guatejug.controller.exceptions.RollbackFailureException;
import org.guatejug.data.Ponencia;

public class PonenciaJpaController implements Serializable {

        public PonenciaJpaController(UserTransaction utx, EntityManagerFactory emf) {
                this.utx = utx;
                this.emf = emf;
        }
        
        public PonenciaJpaController(EntityManagerFactory emf) {
                this.emf = emf;
        }
        
        @Resource
        private UserTransaction utx;
        
        private EntityManagerFactory emf = null;

        public EntityManager getEntityManager() {
                return emf.createEntityManager();
        }

        public void create(Ponencia ponencia) throws RollbackFailureException, Exception {
                if (ponencia.getPonenteList() == null) {
                        ponencia.setPonenteList(new ArrayList<Ponente>());
                }
                EntityManager em = null;
                try {
                        utx.begin();
                        em = getEntityManager();
                        em.getTransaction().begin();
                        em.persist(ponencia);
                        em.getTransaction().commit();
                        utx.commit();
                } catch (Exception ex) {
                        try {
                                utx.rollback();
                        } catch (Exception re) {
                                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
                        }
                        throw ex;
                } finally {
                        if (em != null) {
                                em.close();
                        }
                }
        }

        public void edit(Ponencia ponencia) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
                EntityManager em = null;
                try {
                        utx.begin();
                        em = getEntityManager();
                        Ponencia persistentPonencia = em.find(Ponencia.class, ponencia.getIdPonencia());
                        List<Ponente> ponenteListOld = persistentPonencia.getPonenteList();
                        List<Ponente> ponenteListNew = ponencia.getPonenteList();
                        List<String> illegalOrphanMessages = null;
                        for (Ponente ponenteListOldPonente : ponenteListOld) {
                                if (!ponenteListNew.contains(ponenteListOldPonente)) {
                                        if (illegalOrphanMessages == null) {
                                                illegalOrphanMessages = new ArrayList<String>();
                                        }
                                        illegalOrphanMessages.add("You must retain Ponente " + ponenteListOldPonente + " since its ponencia field is not nullable.");
                                }
                        }
                        if (illegalOrphanMessages != null) {
                                throw new IllegalOrphanException(illegalOrphanMessages);
                        }
                        List<Ponente> attachedPonenteListNew = new ArrayList<Ponente>();
                        for (Ponente ponenteListNewPonenteToAttach : ponenteListNew) {
                                ponenteListNewPonenteToAttach = em.getReference(ponenteListNewPonenteToAttach.getClass(), ponenteListNewPonenteToAttach.getIdPonente());
                                attachedPonenteListNew.add(ponenteListNewPonenteToAttach);
                        }
                        ponenteListNew = attachedPonenteListNew;
                        ponencia.setPonenteList(ponenteListNew);
                        ponencia = em.merge(ponencia);
                        for (Ponente ponenteListNewPonente : ponenteListNew) {
                                if (!ponenteListOld.contains(ponenteListNewPonente)) {
                                        Ponencia oldPonenciaOfPonenteListNewPonente = ponenteListNewPonente.getPonencia();
                                        ponenteListNewPonente.setPonencia(ponencia);
                                        ponenteListNewPonente = em.merge(ponenteListNewPonente);
                                        if (oldPonenciaOfPonenteListNewPonente != null && !oldPonenciaOfPonenteListNewPonente.equals(ponencia)) {
                                                oldPonenciaOfPonenteListNewPonente.getPonenteList().remove(ponenteListNewPonente);
                                                oldPonenciaOfPonenteListNewPonente = em.merge(oldPonenciaOfPonenteListNewPonente);
                                        }
                                }
                        }
                        utx.commit();
                } catch (Exception ex) {
                        try {
                                utx.rollback();
                        } catch (Exception re) {
                                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
                        }
                        String msg = ex.getLocalizedMessage();
                        if (msg == null || msg.length() == 0) {
                                Integer id = ponencia.getIdPonencia();
                                if (findPonencia(id) == null) {
                                        throw new NonexistentEntityException("The ponencia with id " + id + " no longer exists.");
                                }
                        }
                        throw ex;
                } finally {
                        if (em != null) {
                                em.close();
                        }
                }
        }

        public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
                EntityManager em = null;
                try {
                        utx.begin();
                        em = getEntityManager();
                        Ponencia ponencia;
                        try {
                                ponencia = em.getReference(Ponencia.class, id);
                                ponencia.getIdPonencia();
                        } catch (EntityNotFoundException enfe) {
                                throw new NonexistentEntityException("The ponencia with id " + id + " no longer exists.", enfe);
                        }
                        List<String> illegalOrphanMessages = null;
                        List<Ponente> ponenteListOrphanCheck = ponencia.getPonenteList();
                        for (Ponente ponenteListOrphanCheckPonente : ponenteListOrphanCheck) {
                                if (illegalOrphanMessages == null) {
                                        illegalOrphanMessages = new ArrayList<String>();
                                }
                                illegalOrphanMessages.add("This Ponencia (" + ponencia + ") cannot be destroyed since the Ponente " + ponenteListOrphanCheckPonente + " in its ponenteList field has a non-nullable ponencia field.");
                        }
                        if (illegalOrphanMessages != null) {
                                throw new IllegalOrphanException(illegalOrphanMessages);
                        }
                        em.remove(ponencia);
                        utx.commit();
                } catch (Exception ex) {
                        try {
                                utx.rollback();
                        } catch (Exception re) {
                                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
                        }
                        throw ex;
                } finally {
                        if (em != null) {
                                em.close();
                        }
                }
        }

        public List<Ponencia> findPonenciaEntities() {
                return findPonenciaEntities(true, -1, -1);
        }

        public List<Ponencia> findPonenciaEntities(int maxResults, int firstResult) {
                return findPonenciaEntities(false, maxResults, firstResult);
        }

        private List<Ponencia> findPonenciaEntities(boolean all, int maxResults, int firstResult) {
                EntityManager em = getEntityManager();
                try {
                        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
                        cq.select(cq.from(Ponencia.class));
                        Query q = em.createQuery(cq);
                        if (!all) {
                                q.setMaxResults(maxResults);
                                q.setFirstResult(firstResult);
                        }
                        return q.getResultList();
                } finally {
                        //em.close();
                        em.clear();
                }
        }

        public Ponencia findPonencia(Integer id) {
                EntityManager em = getEntityManager();
                try {
                        return em.find(Ponencia.class, id);
                } finally {
                        em.close();
                }
        }

        public int getPonenciaCount() {
                EntityManager em = getEntityManager();
                try {
                        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
                        Root<Ponencia> rt = cq.from(Ponencia.class);
                        cq.select(em.getCriteriaBuilder().count(rt));
                        Query q = em.createQuery(cq);
                        return ((Long) q.getSingleResult()).intValue();
                } finally {
                        em.close();
                }
        }

}
