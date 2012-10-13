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
package org.guatejug.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


@FacesValidator("org.guatejug.util.EmailValidator")
public class EmailValidator implements Validator {

        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\."
                + "[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*"
                + "(\\.[A-Za-z]{2,})$";
        private Pattern pattern;
        private Matcher matcher;

        public EmailValidator() {
                pattern = Pattern.compile(EMAIL_PATTERN);
        }

        @Override
        public void validate(FacesContext context, UIComponent component,
                Object value) throws ValidatorException {

                matcher = pattern.matcher(value.toString());
                if (!matcher.matches()) {
                        //FacesContext.getCurrentInstance().addMessage("form:input-ponentes",
                        FacesMessage msg =
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor introduzca la biografia del ponente", "");
                        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                        
                        throw new ValidatorException(msg);
                }

        }
}