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

import java.util.Properties;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.guatejug.data.Ponencia;
import org.guatejug.data.Ponente;

public class EmailManager {

        public static void postMail(String recipients[], String subject, String message, String from) throws MessagingException {
                boolean debug = false;

                //Set the host smtp address
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.example.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                // create some properties and get the default Session
                /*Session session = Session.getDefaultInstance(props, null);
                session.setDebug(debug);*/
                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(FacesContext.getCurrentInstance().getExternalContext().getInitParameter("org.guatejug.email"),
                                                FacesContext.getCurrentInstance().getExternalContext().getInitParameter("org.guatejug.password"));
                                }
                        });
                // create a message
                Message msg = new MimeMessage(session);

                // set the from and to address
                InternetAddress addressFrom = new InternetAddress(from);
                msg.setFrom(addressFrom);

                InternetAddress[] addressTo = new InternetAddress[recipients.length+1];
                for (int i = 0; i < recipients.length; i++) {
                        addressTo[i] = new InternetAddress(recipients[i]);
                }
                addressTo[recipients.length] = new InternetAddress("tuxtor@shekalug.org");
                msg.setRecipients(Message.RecipientType.BCC, addressTo);

                // Optional : You can also set your custom headers in the Email if you Want
                msg.addHeader("MyHeaderName", "myHeaderValue");

                // Setting the Subject and Content Type
                msg.setSubject(subject);
                msg.setContent(message, "text/plain");
                Transport.send(msg);
        }
        
        public static String generateSimpleEmail(Ponencia ponencia){
                String emailTxt="Su propuesta para la charla: \""+ponencia.getTitulo()+"\" ha sido recibida con exito \n"+
                        "\n"+
                        "Los siguientes ponentes han recibido el email de confirmación: \n\n";
                        for(Ponente ponente:ponencia.getPonenteList()){
                                emailTxt+=ponente.getNombres()+" "+ponente.getApellidos()+" "+ponente.getEmail()+"\n";
                        }
                        emailTxt+="\nLos horarios seran publicados a la mayor brevedad posible tomando en cuenta sus sugerencias\n";
                return emailTxt;
        }
}