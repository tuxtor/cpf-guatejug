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
package org.guatejug.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import org.guatejug.controller.PonenciaJpaController;
import org.guatejug.data.Ponencia;
import org.guatejug.data.Ponente;
import org.guatejug.util.EmailManager;
import org.guatejug.util.NullValidator;

/**
 *
 * @author tuxtor
 */
@ManagedBean
@ViewScoped
public class Index implements Serializable {

        public Map<String, Short> getCategoriaMap() {
                return categoriaMap;
        }
        private List<Ponente> listadoPonentes;
        private Ponente ponente;
        private Ponencia ponencia;
        private Map<String, Short> categoriaMap;
        private Map<String, Short> duracionMap;
        private Map<String, Short> tipoMap;
        private String captchaRecivido;
        private EntityManagerFactory emf;
        //--
        private UserTransaction utx;
        private UIComponent component;
        private boolean propuestaConfirmada;

        static {
        }

        /**
         * Creates a new instance of Index
         */
        public Index() {
                ponente = new Ponente();
                ponencia = new Ponencia();
                listadoPonentes = new ArrayList<Ponente>();

                categoriaMap = new LinkedHashMap<String, Short>();
                categoriaMap.put("Java SE", (short) 0);
                categoriaMap.put("Java ME/Android", (short) 1);
                categoriaMap.put("Java EE/Java Web/Frameworks", (short) 2);
                categoriaMap.put("Otros lenguajes JVM", (short) 3);
                categoriaMap.put("JavaFX", (short) 4);
                categoriaMap.put("Comunidad", (short) 5);

                duracionMap = new LinkedHashMap<String, Short>();
                duracionMap.put("30 minutos", (short) 0);
                duracionMap.put("60 minutos", (short) 1);

                tipoMap = new LinkedHashMap<String, Short>();
                tipoMap.put("Conferencia", (short) 0);
                tipoMap.put("Taller", (short) 1);

        }

        public String persistirPonencia() {
                //Validaciones
                FacesContext context = FacesContext
                        .getCurrentInstance();
                ExternalContext ext = context.getExternalContext();
                Map<String, Object> session = ext.getSessionMap();
                String captchaEsperado = session
                        .get(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY).toString();
                if (!captchaEsperado.equalsIgnoreCase(captchaRecivido)) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al introducir el codigo captcha", "Error al introducir el codigo captcha"));
                        this.captchaRecivido = "";
                        return null;
                }

                if (!propuestaConfirmada) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe confirmar que essu charla propuesta no esta en contra, ni utiliza material que pueda implicar problemas con derechos de autor", "Debe confirmar que essu charla propuesta no esta en contra, ni utiliza material que pueda implicar problemas con derechos de autor"));
                        this.captchaRecivido = "";
                        return null;
                }

                if (listadoPonentes == null || listadoPonentes.size() <= 0) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe agregar al menos un ponente", "Debe agregar al menos un ponente"));
                        this.captchaRecivido = "";
                        return null;
                }

                ponencia.setPonenteList(listadoPonentes);

                for (Ponente ponenteObj : listadoPonentes) {
                        ponenteObj.setPonencia(ponencia);
                }
                NullValidator.performObjectValidation(ponencia);
                try {
                        InitialContext ic = new InitialContext();
                        utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                        emf = Persistence.createEntityManagerFactory("cpf-guatejugPU");
                        PonenciaJpaController controladorPonencia = new PonenciaJpaController(utx, emf);
                        controladorPonencia.create(ponencia);
                        try {
                                String[] arrayDestinatarios = new String[ponencia.getPonenteList().size()];
                                int i = 0;
                                for (Ponente pon : ponencia.getPonenteList()) {
                                        arrayDestinatarios[i] = pon.getEmail();
                                        i++;
                                }
                                EmailManager.postMail(arrayDestinatarios,
                                        "Confirmacion de charla 2012",
                                        EmailManager.generateSimpleEmail(ponencia),
                                        "info@guate-jug.net");
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        return "exito.xhtml?faces-redirect=true";
                } catch (Exception e) {
                        e.printStackTrace();
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrio un problema grave al grabar su propuesta, por favor contacte a info@guate-jug.net", "Ocurrio un problema grave al grabar su propuesta, por favor contacte a info@guate-jug.net"));
                        return "";

                }

        }

        public void agregarPonente() {
                if (listadoPonentes != null) {
                        if (listadoPonentes.size() == 3) {
                                FacesContext.getCurrentInstance().addMessage("form:input-ponentes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se permiten mas de tres ponentes", "No se permiten mas de tres ponentes"));
                                return;
                        }
                }

                //Validaciones de objeto
                if (ponente.getNombres().isEmpty()) {
                        FacesContext.getCurrentInstance().addMessage("form:input-ponentes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor introduzca el nombre del ponente", ""));
                        return;
                }
                if (ponente.getApellidos().isEmpty()) {
                        FacesContext.getCurrentInstance().addMessage("form:input-ponentes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor introduzca los apellidos del ponente", ""));
                        return;
                }
                if (ponente.getEmail().isEmpty()) {
                        FacesContext.getCurrentInstance().addMessage("form:input-ponentes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor introduzca el email del ponente", ""));
                        return;
                }
                if (ponente.getBiografia().isEmpty()) {
                        FacesContext.getCurrentInstance().addMessage("form:input-ponentes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor introduzca la biografia del ponente", ""));
                        return;
                }

                for (Ponente ponenteListado : listadoPonentes) {
                        if (ponenteListado.getEmail().equalsIgnoreCase(ponente.getEmail())) {
                                FacesContext.getCurrentInstance().addMessage("form:input-ponentes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "El ponente con el email: " + ponente.getEmail() + " ya ha sido agregado", ""));
                                return;
                        }
                }
                getListadoPonentes().add(ponente);
                ponente = new Ponente();
        }

        public void eliminarPonente(Ponente po) {
                listadoPonentes.remove(po);
        }

        //--
        public Ponencia getPonencia() {
                return ponencia;
        }

        public void setPonencia(Ponencia ponencia) {
                this.ponencia = ponencia;
        }

        public List<Ponente> getListadoPonentes() {
                return listadoPonentes == null ? new ArrayList<Ponente>() : listadoPonentes;
        }

        public void setListadoPonentes(List<Ponente> listadoPonentes) {
                this.listadoPonentes = listadoPonentes;
        }

        public Ponente getPonente() {
                return ponente;
        }

        public void setPonente(Ponente ponente) {
                this.ponente = ponente;
        }

        public Map<String, Short> getDuracionMap() {
                return duracionMap;
        }

        public void setDuracionMap(Map<String, Short> duracionMap) {
                this.duracionMap = duracionMap;
        }

        public Map<String, Short> getTipoMap() {
                return tipoMap;
        }

        public void setTipoMap(Map<String, Short> tipoMap) {
                this.tipoMap = tipoMap;
        }

        public String getCaptchaRecivido() {
                return captchaRecivido;
        }

        public void setCaptchaRecivido(String captchaRecivido) {
                this.captchaRecivido = captchaRecivido;
        }

        public UIComponent getComponent() {
                return component;
        }

        public void setComponent(UIComponent component) {
                this.component = component;
        }

        public boolean isPropuestaConfirmada() {
                return propuestaConfirmada;
        }

        public void setPropuestaConfirmada(boolean propuestaConfirmada) {
                this.propuestaConfirmada = propuestaConfirmada;
        }
}
