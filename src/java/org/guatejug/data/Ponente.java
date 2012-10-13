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
package org.guatejug.data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.guatejug.util.NullValidator;

@Entity
@Table(name = "ponente")
@NamedQueries({
        @NamedQuery(name = "Ponente.findAll", query = "SELECT p FROM Ponente p")})

@TableGenerator(name = "PonenteGen", table = "secuencia",
pkColumnName = "id_secuencia", valueColumnName = "valor",
pkColumnValue = "Ponente", allocationSize = 1)
public class Ponente implements Serializable {

        private static final long serialVersionUID = 1L;
                
        @Id
        @Basic(optional = false)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "PonenteGen")
        @Column(name = "id_ponente")
        private int idPonente;
        @Basic(optional = false)
        
        @Size(min = 1, max = 250)
        @Column(name = "nombres")
        private String nombres;
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 250)
        @Column(name = "apellidos")
        private String apellidos;
        // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 50)
        @Column(name = "email")
        private String email;
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 2000)
        @Column(name = "biografia")
        private String biografia;
        @Basic(optional = true)
        @Column(name = "observaciones")
        private String observaciones;
        @JoinColumn(name = "id_ponencia", referencedColumnName = "id_ponencia")
        @ManyToOne(optional = false)
        private Ponencia ponencia;

        public Ponente() {
                NullValidator.performObjectValidation(this);
        }

        public Ponente(String nombres, String apellidos, String email, String biografia, String observaciones) {
                this.nombres = nombres;
                this.apellidos = apellidos;
                this.email = email;
                this.biografia = biografia;
                this.observaciones = observaciones;
        }

        public String getNombres() {
                return nombres;
        }

        public void setNombres(String nombres) {
                this.nombres = nombres;
        }

        public String getApellidos() {
                return apellidos;
        }

        public void setApellidos(String apellidos) {
                this.apellidos = apellidos;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getBiografia() {
                return biografia;
        }

        public void setBiografia(String biografia) {
                this.biografia = biografia;
        }

        public String getObservaciones() {
                return observaciones;
        }

        public void setObservaciones(String observaciones) {
                this.observaciones = observaciones;
        }

        public Ponencia getPonencia() {
                return ponencia;
        }

        public void setPonencia(Ponencia ponencia) {
                this.ponencia = ponencia;
        }

        @Override
        public int hashCode() {
                int hash = 0;
                hash += getIdPonente();
                return hash;
        }

        @Override
        public boolean equals(Object object) {
                // TODO: Warning - this method won't work in the case the id fields are not set
                if (!(object instanceof Ponente)) {
                        return false;
                }
                Ponente other = (Ponente) object;
                if (this.getIdPonente() != other.getIdPonente()) {
                        return false;
                }
                return true;
        }

        @Override
        public String toString() {
                return "org.guatejug.persistence.Ponente[ ponentePK=" + getIdPonente() + " ]";
        }

        public int getIdPonente() {
                return idPonente;
        }

        public void setIdPonente(int idPonente) {
                this.idPonente = idPonente;
        }
}
