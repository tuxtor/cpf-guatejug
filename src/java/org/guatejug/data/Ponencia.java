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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.guatejug.util.NullValidator;

@Entity
@Table(name = "ponencia")
@NamedQueries({
        @NamedQuery(name = "Ponencia.findAll", query = "SELECT p FROM Ponencia p")})
@TableGenerator(name = "PonenciaGen", table = "secuencia",
pkColumnName = "id_secuencia", valueColumnName = "valor",
pkColumnValue = "Ponencia", allocationSize = 1)
public class Ponencia implements Serializable {

        private static final long serialVersionUID = 1L;
        @Id
        @Basic(optional = false)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "PonenciaGen")
        @Column(name = "id_ponencia")
        private int idPonencia;
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 250)
        @Column(name = "titulo")
        private String titulo;
        @Basic(optional = false)
        @NotNull
        @Column(name = "categoria")
        private short categoria;
        @Basic(optional = false)
        @NotNull
        @Column(name = "tipo")
        private short tipo;
        @Basic(optional = false)
        @NotNull
        @Column(name = "nivel")
        private short nivel;
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 2000)
        @Column(name = "resumen")
        private String resumen;
        @Basic(optional = false)
        @NotNull
        @Column(name = "duracion")
        private short duracion;
        @Basic(optional = false)
        @NotNull
        @Column(name = "datashow")
        private short datashow;
        @Basic(optional = false)
        @NotNull
        @Column(name = "laptop")
        private short laptop;
        @Basic(optional = true)
        @Column(name = "otros")
        private String otros;
        @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "ponencia")
        private List<Ponente> ponenteList;

        public Ponencia() {
                NullValidator.performObjectValidation(this);
                this.datashow=1;
        }

        public Ponencia(Integer idPonencia) {
                this.idPonencia = idPonencia;
        }

        public Ponencia(Integer idPonencia, String titulo, short categoria, short tipo, short nivel, String resumen, short duracion, short datashow, short laptop, String otros) {
                this.idPonencia = idPonencia;
                this.titulo = titulo;
                this.categoria = categoria;
                this.tipo = tipo;
                this.nivel = nivel;
                this.resumen = resumen;
                this.duracion = duracion;
                this.datashow = datashow;
                this.laptop = laptop;
                this.otros = otros;
        }

        public Integer getIdPonencia() {
                return idPonencia;
        }

        public void setIdPonencia(Integer idPonencia) {
                this.idPonencia = idPonencia;
        }

        public String getTitulo() {
                return titulo;
        }

        public void setTitulo(String titulo) {
                this.titulo = titulo;
        }

        public short getCategoria() {
                return categoria;
        }

        public void setCategoria(short categoria) {
                this.categoria = categoria;
        }

        public short getTipo() {
                return tipo;
        }

        public void setTipo(short tipo) {
                this.tipo = tipo;
        }

        public short getNivel() {
                return nivel;
        }

        public void setNivel(short nivel) {
                this.nivel = nivel;
        }

        public String getResumen() {
                return resumen;
        }

        public void setResumen(String resumen) {
                this.resumen = resumen;
        }

        public short getDuracion() {
                return duracion;
        }

        public void setDuracion(short duracion) {
                this.duracion = duracion;
        }

        public short getDatashow() {
                return datashow;
        }

        public void setDatashow(short datashow) {
                this.datashow = datashow;
        }

        public short getLaptop() {
                return laptop;
        }

        public void setLaptop(short laptop) {
                this.laptop = laptop;
        }

        public String getOtros() {
                return otros;
        }

        public void setOtros(String otros) {
                this.otros = otros;
        }

        public List<Ponente> getPonenteList() {
                return ponenteList;
        }

        public void setPonenteList(List<Ponente> ponenteList) {
                this.ponenteList = ponenteList;
        }

        public boolean getBdataShow() {
                return datashow == 1 ? true : false;
        }

        public void setBdataShow(boolean value) {
                datashow = value == true ? (short) 1 : (short) 0;
        }

        public boolean getBlaptop() {
                return laptop == 1 ? true : false;
        }

        public void setBlaptop(boolean value) {
                laptop = value == true ? (short) 1 : (short) 0;
        }

        @Override
        public int hashCode() {
                int hash = 0;
                hash += (int) idPonencia;
                return hash;
        }

        @Override
        public boolean equals(Object object) {
                // TODO: Warning - this method won't work in the case the id fields are not set
                if (!(object instanceof Ponencia)) {
                        return false;
                }
                Ponencia other = (Ponencia) object;
                if (this.idPonencia != other.idPonencia) {
                        return false;
                }
                return true;
        }

        @Override
        public String toString() {
                return "org.guatejug.persistence.Ponencia[ idPonencia=" + idPonencia + " ]";
        }
}
