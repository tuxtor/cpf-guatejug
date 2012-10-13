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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class NullValidator {

        /**
         * This method performs a step by step validation over object properties
         * using reflection based on the methods list if a method is a get kind
         * method his equivalent set or is method is searched and invoked to
         * initialize the value avoiding nulls in the properties
         *
         * @param pObject
         */
        public static void performObjectValidation(Object pObject) {
                Method metodos[] = pObject.getClass().getMethods();
                for (int i = 0; i < metodos.length; i++) {
                        Method metodo = metodos[i];
                        //Si es un metodo get o is lo utilizo con su equivalente set
                        if ((metodo.getName().substring(0, 3).equalsIgnoreCase("get") || metodo.getName().substring(0, 2).equalsIgnoreCase("is")) && !metodo.getName().equals("getClass")) {
                                String methodNameSet = "";
                                if (metodo.getName().substring(0, 3).equalsIgnoreCase("get")) {
                                        methodNameSet = metodo.getName().replaceFirst("get", "set");
                                } else {
                                        methodNameSet = methodNameSet.replaceFirst("is", "set");
                                }
                                try {
                                        Method metodoSet = pObject.getClass().getMethod(methodNameSet, metodo.getReturnType());
                                        if (metodoSet != null) {
                                                //Numeric data
                                                //byte
                                                if (metodo.getReturnType().equals(java.lang.Byte.class)) {
                                                        Byte valor = (Byte) metodo.invoke(pObject, new Object[0]);
                                                        if (valor == null) {
                                                                metodoSet.invoke(pObject, 0);
                                                        }
                                                } else if (metodo.getReturnType().equals(java.math.BigDecimal.class)) {//BigDecimal
                                                        BigDecimal valor = (BigDecimal) metodo.invoke(pObject, new Object[0]);
                                                        if (valor == null) {
                                                                metodoSet.invoke(pObject, new BigDecimal(0));
                                                        }
                                                } else if (metodo.getReturnType().equals(java.lang.Double.class)) {//Double
                                                        Double valor = (Double) metodo.invoke(pObject, new Object[0]);
                                                        if (valor == null) {
                                                                metodoSet.invoke(pObject, new Double(0));
                                                        }
                                                } else if (metodo.getReturnType().equals(java.lang.String.class)) {//String
                                                        String valor = (String) metodo.invoke(pObject, new Object[0]);
                                                        if (valor == null) {
                                                                metodoSet.invoke(pObject, "");
                                                        }
                                                } else if (metodo.getReturnType().equals(java.util.List.class)) {//List
                                                        List objetosList = (List) metodo.invoke(pObject, new Object[0]);
                                                        for (Object objetoFromList : objetosList) {
                                                                NullValidator.performObjectValidation(objetoFromList);
                                                        }
                                                } else if (metodo.getReturnType().equals(java.util.Date.class)) {//Date
                                                        Date valor = (Date) metodo.invoke(pObject, new Object[0]);
                                                        if (valor == null) {
                                                                metodoSet.invoke(pObject, new Date());
                                                        }
                                                } else if (metodo.getReturnType().isPrimitive()) {//Primitive
                                                        //Nothing to do here
                                                }
                                        }
                                } catch (Exception e) {
                                }
                        }
                }
        }
}
