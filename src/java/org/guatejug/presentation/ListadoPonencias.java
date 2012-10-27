/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guatejug.presentation;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import org.guatejug.controller.PonenciaJpaController;
import org.guatejug.data.Ponencia;

/**
 *
 * @author tuxtor
 */
@ManagedBean
@ViewScoped
public class ListadoPonencias {

        private EntityManagerFactory emf;
        //--
        private UserTransaction utx;
        private Ponencia selectedPonencia;
        private HtmlDataTable ponentesDataTable;
        private List<Ponencia> listadoPonencias = new ArrayList<Ponencia>();

        /**
         * Creates a new instance of ListadoPonencias
         */
        public ListadoPonencias() {
                selectedPonencia = new Ponencia();
        }

        public void setSelectedPonencia(int idPonencia) {
                for(Ponencia ponencia:listadoPonencias){
                        if(ponencia.getIdPonencia()==idPonencia){
                                this.selectedPonencia = ponencia;
                                break;
                        }
                        
                }
        }

        public List<Ponencia> getListadoPonencias() {
                if (listadoPonencias == null || listadoPonencias.isEmpty()) {
                        PonenciaJpaController controladorPonencia = new PonenciaJpaController(utx, emf);
                        try {
                                InitialContext ic = new InitialContext();
                                utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                                emf = Persistence.createEntityManagerFactory("cpf-guatejugPU");
                                listadoPonencias = new ArrayList<Ponencia>(controladorPonencia.findPonenciaEntities());
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
                
                return listadoPonencias;
        }

        public Ponencia getSelectedPonencia() {
                return selectedPonencia;
        }

        public HtmlDataTable getPonentesDataTable() {
                return ponentesDataTable;
        }

        public void setPonentesDataTable(HtmlDataTable ponentesDataTable) {
                this.ponentesDataTable = ponentesDataTable;
        }
}
