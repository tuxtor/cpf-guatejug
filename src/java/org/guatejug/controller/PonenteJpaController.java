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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import org.guatejug.controller.exceptions.NonexistentEntityException;
import org.guatejug.controller.exceptions.RollbackFailureException;
import org.guatejug.data.Ponencia;
import org.guatejug.data.Ponente;

public class PonenteJpaController implements Serializable {

        public PonenteJpaController(UserTransaction utx, EntityManagerFactory emf) {
                this.utx = utx;
                this.emf = emf;
        }
        private UserTransaction utx = null;
        private EntityManagerFactory emf = null;

        public EntityManager getEntityManager() {
                return emf.createEntityManager();
        }

        public void create(Ponente ponente) throws RollbackFailureException, Exception {
                EntityManager em = null;
                try {
                        utx.begin();
                        em = getEntityManager();
                        Ponencia ponencia = ponente.getPonencia();
                        if (ponencia != null) {
                                ponencia = em.getReference(ponencia.getClass(), ponencia.getIdPonencia());
                                ponente.setPonencia(ponencia);
                        }
                        em.persist(ponente);
                        if (ponencia != null) {
                                ponencia.getPonenteList().add(ponente);
                                ponencia = em.merge(ponencia);
                        }
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

        public void edit(Ponente ponente) throws NonexistentEntityException, RollbackFailureException, Exception {
                EntityManager em = null;
                try {
                        utx.begin();
                        em = getEntityManager();
                        Ponente persistentPonente = em.find(Ponente.class, ponente.getIdPonente());
                        Ponencia ponenciaOld = persistentPonente.getPonencia();
                        Ponencia ponenciaNew = ponente.getPonencia();
                        if (ponenciaNew != null) {
                                ponenciaNew = em.getReference(ponenciaNew.getClass(), ponenciaNew.getIdPonencia());
                                ponente.setPonencia(ponenciaNew);
                        }
                        ponente = em.merge(ponente);
                        if (ponenciaOld != null && !ponenciaOld.equals(ponenciaNew)) {
                                ponenciaOld.getPonenteList().remove(ponente);
                                ponenciaOld = em.merge(ponenciaOld);
                        }
                        if (ponenciaNew != null && !ponenciaNew.equals(ponenciaOld)) {
                                ponenciaNew.getPonenteList().add(ponente);
                                ponenciaNew = em.merge(ponenciaNew);
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
                                int id = ponente.getIdPonente();
                                if (findPonente(id) == null) {
                                        throw new NonexistentEntityException("The ponente with id " + id + " no longer exists.");
                                }
                        }
                        throw ex;
                } finally {
                        if (em != null) {
                                em.close();
                        }
                }
        }

        public void destroy(int id) throws NonexistentEntityException, RollbackFailureException, Exception {
                EntityManager em = null;
                try {
                        utx.begin();
                        em = getEntityManager();
                        Ponente ponente;
                        try {
                                ponente = em.getReference(Ponente.class, id);
                                ponente.getIdPonente();
                        } catch (EntityNotFoundException enfe) {
                                throw new NonexistentEntityException("The ponente with id " + id + " no longer exists.", enfe);
                        }
                        Ponencia ponencia = ponente.getPonencia();
                        if (ponencia != null) {
                                ponencia.getPonenteList().remove(ponente);
                                ponencia = em.merge(ponencia);
                        }
                        em.remove(ponente);
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

        public List<Ponente> findPonenteEntities() {
                return findPonenteEntities(true, -1, -1);
        }

        public List<Ponente> findPonenteEntities(int maxResults, int firstResult) {
                return findPonenteEntities(false, maxResults, firstResult);
        }

        private List<Ponente> findPonenteEntities(boolean all, int maxResults, int firstResult) {
                EntityManager em = getEntityManager();
                try {
                        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
                        cq.select(cq.from(Ponente.class));
                        Query q = em.createQuery(cq);
                        if (!all) {
                                q.setMaxResults(maxResults);
                                q.setFirstResult(firstResult);
                        }
                        return q.getResultList();
                } finally {
                        em.close();
                }
        }

        public Ponente findPonente(int id) {
                EntityManager em = getEntityManager();
                try {
                        return em.find(Ponente.class, id);
                } finally {
                        em.close();
                }
        }

        public int getPonenteCount() {
                EntityManager em = getEntityManager();
                try {
                        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
                        Root<Ponente> rt = cq.from(Ponente.class);
                        cq.select(em.getCriteriaBuilder().count(rt));
                        Query q = em.createQuery(cq);
                        return ((Long) q.getSingleResult()).intValue();
                } finally {
                        em.close();
                }
        }

}
