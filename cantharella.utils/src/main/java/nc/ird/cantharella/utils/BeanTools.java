/*
 * #%L
 * Cantharella :: Utils
 * $Id: BeanTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/BeanTools.java $
 * %%
 * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package nc.ird.cantharella.utils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Tools for beans
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class BeanTools {

    /**
     * Access type
     */
    public enum AccessType {
        /** Access by (public) field */
        FIELD,
        /** Access by (public) getter */
        GETTER;
    }

    /** Error message for annotation not found in a class */
    private static final String ANNOTATION = "Argument of type %s must have a field annotated with %s";

    /** Error message for field not found in a class */
    private static final String FIELD = "Argument of type %s must have an accessible %s property";

    /**
     * Equals method
     * 
     * @param thiz First object ("this", not null)
     * @param obj Second object
     * @param accessType Access type
     * @param properties Properties (names) to check
     * @return Equality
     */
    public static boolean equals(Object thiz, Object obj, AccessType accessType, String... properties) {
        AssertTools.assertNotNull(thiz);
        AssertTools.assertNotEmpty(properties);
        AssertTools.assertArrayNotNull(properties);
        if (thiz == obj) {
            return true;
        }
        if (obj != null
                && thiz != null
                && (thiz.getClass().isAssignableFrom(obj.getClass()) || obj.getClass()
                        .isAssignableFrom(thiz.getClass()))) {
            EqualsBuilder builder = new EqualsBuilder();
            for (String property : properties) {
                try {
                    Object val1 = getValue(thiz, accessType, property);
                    Object val2 = getValue(obj, accessType, property);

                    // Test hashCodes first
                    if (val1 != null && val2 != null && !builder.append(val1.hashCode(), val2.hashCode()).isEquals()) {
                        return false;
                    }

                    if (!builder.append(val1, val2).isEquals()) {
                        return false;
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format(FIELD, thiz.getClass(), property), e);
                }

            }
            return builder.isEquals();
        }
        return false;
    }

    /**
     * Equals method
     * 
     * @param thiz First object ("this", not null)
     * @param obj Second object
     * @param fields Public fields to check
     * @return Equality
     */
    public static boolean equals(Object thiz, Object obj, Field... fields) {
        AssertTools.assertNotNull(thiz);
        AssertTools.assertNotEmpty(fields);
        AssertTools.assertArrayNotNull(fields);
        if (thiz == obj) {
            return true;
        }
        if (obj != null
                && thiz != null
                && (thiz.getClass().isAssignableFrom(obj.getClass()) || obj.getClass()
                        .isAssignableFrom(thiz.getClass()))) {
            EqualsBuilder builder = new EqualsBuilder();
            for (Field field : fields) {
                try {
                    Object val1 = field.get(thiz);
                    Object val2 = field.get(obj);

                    // Test hashCodes first
                    if (val1 != null && val2 != null && !builder.append(val1.hashCode(), val2.hashCode()).isEquals()) {
                        return false;
                    }

                    if (!builder.append(val1, val2).isEquals()) {
                        return false;
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format(FIELD, thiz.getClass(), field.getName()), e);
                }
            }
            return builder.isEquals();
        }
        return false;
    }

    /**
     * Retrieve an annotated public field or from inherited public field too
     * 
     * @param thiz Object
     * @param annotations Annotations to retrieve on a public field
     * @return First annotated public field
     * @throws NoSuchFieldException If an annotation cannot be found
     */
    @SafeVarargs
    public static Field getAnnotatedPublicField(Object thiz, Class<? extends Annotation>... annotations)
            throws NoSuchFieldException {
        AssertTools.assertNotNull(thiz);
        return getAnnotatedPublicField(thiz.getClass(), annotations);
    }

    /**
     * Retrieve an annotated from private field or from inherited private, protected or public field
     * 
     * @param clazz The class which contains annotations (or inherited class)
     * @param annotations Annotations to retrieve
     * @return First annotated field finded
     * @throws NoSuchFieldException If an annotation cannot be found
     */
    @SafeVarargs
    public static Field getAnnotatedPrivateField(Class<?> clazz, Class<? extends Annotation>... annotations)
            throws NoSuchFieldException {
        AssertTools.assertNotNull(clazz);
        AssertTools.assertNotEmpty(annotations);
        AssertTools.assertArrayNotNull(annotations);
        Field[] fields = clazz.getDeclaredFields();
        Field field = null;
        int i = 0;
        while (field == null && i < fields.length) {
            int j = 0;
            while (field == null && j < annotations.length) {
                if (fields[i].isAnnotationPresent(annotations[j])) {
                    field = fields[i];
                }
                ++j;
            }
            ++i;
        }
        if (field == null) {
            // if not found
            if (clazz.getSuperclass() == null) {
                throw new NoSuchFieldException(String.format(ANNOTATION, clazz.getName(), Arrays.toString(annotations)));
            }
            // recursive search in the superclass
            return getAnnotatedInheritedField(clazz, clazz.getSuperclass(), annotations);
        }
        return field;
    }

    /**
     * Retrieve an annotated from inherited private, protected or public field
     * 
     * @param baseClazz The base clazz where the search began
     * @param browsedClazz The
     * @param annotations Annotations to retrieve
     * @return First annotated field finded
     * @throws NoSuchFieldException If an annotation cannot be found
     */
    @SafeVarargs
    private static Field getAnnotatedInheritedField(Class<?> baseClazz, Class<?> browsedClazz,
            Class<? extends Annotation>... annotations) throws NoSuchFieldException {
        AssertTools.assertNotNull(baseClazz);
        AssertTools.assertNotEmpty(annotations);
        AssertTools.assertArrayNotNull(annotations);

        Field[] fields = browsedClazz.getDeclaredFields();
        Field field = null;
        int i = 0;
        while (field == null && i < fields.length) {
            int j = 0;
            while (field == null && j < annotations.length) {
                if (fields[i].isAnnotationPresent(annotations[j])
                        && (Modifier.isProtected(fields[i].getModifiers())
                                || Modifier.isPublic(fields[i].getModifiers()) || Modifier.isPrivate(fields[i]
                                .getModifiers()))) {
                    field = fields[i];
                }
                ++j;
            }
            ++i;
        }
        if (field == null) {
            // if not found
            if (browsedClazz.getSuperclass() == null) {
                throw new NoSuchFieldException(String.format(ANNOTATION, baseClazz.getName(),
                        Arrays.toString(annotations)));
            }
            // recursive search in the superclass
            getAnnotatedInheritedField(baseClazz, browsedClazz.getSuperclass(), annotations);
        }
        return field;
    }

    /**
     * Retrieve an annotated public field
     * 
     * @param clazz The class which contains annotations
     * @param annotations Annotations to retrieve on a public field
     * @return First annotated public field
     * @throws NoSuchFieldException If an annotation cannot be found
     */
    @SafeVarargs
    public static Field getAnnotatedPublicField(Class<?> clazz, Class<? extends Annotation>... annotations)
            throws NoSuchFieldException {
        AssertTools.assertNotNull(clazz);
        AssertTools.assertNotEmpty(annotations);
        AssertTools.assertArrayNotNull(annotations);
        Field[] fields = clazz.getFields();
        Field field = null;
        int i = 0;
        while (field == null && i < fields.length) {
            int j = 0;
            while (field == null && j < annotations.length) {
                if (fields[i].isAnnotationPresent(annotations[j])) {
                    field = fields[i];
                }
                ++j;
            }
            ++i;
        }
        if (field == null) {
            throw new NoSuchFieldException(String.format(ANNOTATION, clazz.getName(), Arrays.toString(annotations)));
        }
        return field;
    }

    /**
     * Get an annotation on a bean property
     * 
     * @param <A> Annotation class
     * @param beanClass Bean class
     * @param annotation Annotation
     * @param property Property name
     * @return The annotation
     * @throws NoSuchFieldException If the property does not exist
     */
    public static <A extends Annotation> A getAnnotationOnProperty(Class<?> beanClass, Class<A> annotation,
            String property) throws NoSuchFieldException {
        AssertTools.assertNotNull(beanClass);
        AssertTools.assertNotNull(annotation);
        return beanClass.getField(property).getAnnotation(annotation);
    }

    /**
     * Get a property value for a specified bean
     * 
     * @param bean Bean
     * @param accessType Access type
     * @param property Property name
     * @return Property value
     */
    public static Object getValue(Object bean, AccessType accessType, String property) {
        AssertTools.assertNotNull(bean);
        AssertTools.assertNotNull(accessType);
        AssertTools.assertNotEmpty(property);
        Object value;
        switch (accessType) {
        case GETTER:
            try {
                PropertyDescriptor prodDesc = new PropertyDescriptor(property, bean.getClass(), "is"
                        + StringUtils.capitalize(property), null);
                Method method = prodDesc.getReadMethod();
                value = method.invoke(bean);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            break;
        case FIELD:
            try {
                value = bean.getClass().getField(property).get(bean);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
        return value;
    }

    /**
     * Get a property value for a specified bean
     * 
     * @param bean Bean
     * @param accessType Access type
     * @param pathToProperty properties path to access the property, example : beanX.beanY.propZ
     * @return Property value
     */
    public static Object getValueFromPath(Object bean, AccessType accessType, String pathToProperty) {
        AssertTools.assertNotNull(bean);
        AssertTools.assertNotNull(accessType);
        AssertTools.assertNotEmpty(pathToProperty);
        List<String> beanNames = createAccessBeanList(pathToProperty);
        Object curBean = bean;
        for (String beanName : beanNames) {
            if (curBean != null) {
                curBean = BeanTools.getValue(curBean, AccessType.GETTER, beanName);
            }
        }
        return curBean;
    }

    /**
     * Extract from the given path, the differents beans name to access example : "X.Y.Z.F" will give the the array
     * {"X", "Y", "Z", "F"}
     * 
     * @param propertiesPath the properties path to browse
     * @return an array of all the different bean names to access
     */
    private static List<String> createAccessBeanList(final String propertiesPath) {
        final Matcher matcher = Pattern.compile("\\w+").matcher(propertiesPath);
        List<String> paramList = new ArrayList<String>();
        while (matcher.find()) {
            paramList.add(propertiesPath.substring(matcher.start(), matcher.end()));
        }
        return paramList;
    }

    /**
     * HashCode method
     * 
     * @param thiz Bean (not null)
     * @param fields Public fields to considere
     * @return HashCode
     */
    public static int hashCode(Object thiz, Field... fields) {
        AssertTools.assertNotNull(thiz);
        AssertTools.assertNotEmpty(fields);
        AssertTools.assertArrayNotNull(fields);
        int seed = thiz.getClass().hashCode();
        int hashCode = seed;
        for (Field field : fields) {
            Object value;
            try {
                value = field.get(thiz);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format(FIELD, thiz.getClass(), field.getName()), e);
            }
            hashCode += value != null ? seed ^ value.hashCode() : 0;
        }
        return hashCode;
    }

    /**
     * HashCode method
     * 
     * @param thiz Bean (not null)
     * @param ids Bean IDs
     * @return HashCode
     */
    public static int hashCode(Object thiz, Object... ids) {
        AssertTools.assertNotNull(thiz);
        AssertTools.assertNotEmpty(ids);
        AssertTools.assertNotNull(ids);
        int seed = thiz.getClass().hashCode();
        int hashCode = seed;
        for (Object id : ids) {
            hashCode += id != null ? seed ^ id.hashCode() : 0;
        }
        return hashCode;
    }

    /**
     * Is there an annotation on a bean property
     * 
     * @param <A> Annotation class
     * @param beanClass Bean class
     * @param annotation Annotation
     * @param property Property name
     * @return TRUE if the annotation is on the bean property
     * @throws NoSuchFieldException If the property does not exist
     */
    public static <A extends Annotation> boolean isAnnotationOnProperty(Class<?> beanClass, Class<A> annotation,
            String property) throws NoSuchFieldException {
        return getAnnotationOnProperty(beanClass, annotation, property) != null;
    }

    /**
     * Display a bean by listing the specified properties
     * 
     * @param bean Bean
     * @param delimiter Delimiter between properties in the resulting string
     * @param accessType Access type
     * @param pathsToProperties Path of properties to display
     * @return Bean display
     */
    public static String toString(Object bean, String delimiter, AccessType accessType, String... pathsToProperties) {
        AssertTools.assertNotNull(bean);
        AssertTools.assertNotEmpty(delimiter);
        AssertTools.assertArrayNotNull(pathsToProperties);
        Class<?> clazz = bean.getClass();
        StringBuilder builder = new StringBuilder(clazz.getName());
        for (String pathToProperty : pathsToProperties) {
            try {
                Object value = getValueFromPath(bean, accessType, pathToProperty);
                builder.append(delimiter + pathToProperty + ": " + (value != null ? value.toString() : "<null>"));
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format(FIELD, clazz, pathToProperty), e);
            }
        }
        return builder.toString();
    }

    /**
     * Display a bean by listing the specified properties (separated by "\n")
     * 
     * @param bean Bean
     * @param accessType Access type
     * @param properties Properties to display
     * @return Bean display
     */
    public static String toString(Object bean, AccessType accessType, String... properties) {
        return toString(bean, "\n- ", accessType, properties);
    }

    /**
     * Constructor (prevents instantiation)
     */
    private BeanTools() {
        //
    }

    /**
     * Display a list of beans by listing the specified properties (bean separated by "\n", properties by ", ")
     * 
     * @param beansList The beans list to browsed
     * @param accessType Access type
     * @param pathsToProperties Paths to properties to display
     * @return Bean display
     */
    public static String beanListToString(List<? extends Object> beansList, AccessType accessType,
            String... pathsToProperties) {
        return BeanTools.beanListToString(beansList, "\n", ", ", accessType, pathsToProperties);
    }

    /**
     * Display a list of beans by listing the specified properties
     * 
     * @param beansList The beans list to browsed
     * @param delimiterBeans Delimiter between beans in the resulting string
     * @param delimiterProperties Delimiter between properties in the resulting string
     * @param accessType Access type
     * @param pathsToProperties Paths to properties to display
     * @return Bean display
     */
    public static String beanListToString(List<? extends Object> beansList, String delimiterBeans,
            String delimiterProperties, AccessType accessType, String... pathsToProperties) {
        AssertTools.assertNotNull(beansList);
        AssertTools.assertNotEmpty(delimiterProperties);
        AssertTools.assertNotEmpty(delimiterBeans);
        AssertTools.assertNotNull(accessType);
        StringBuilder builder = new StringBuilder();
        Iterator<? extends Object> itBeans = beansList.iterator();
        while (itBeans.hasNext()) {
            Object bean = itBeans.next();
            builder.append(toString(bean, delimiterProperties, accessType, pathsToProperties));
            if (itBeans.hasNext()) {
                builder.append(delimiterBeans);
            }
        }
        return builder.toString();
    }

    /**
     * Wrapping of Apache communs BeanComparator. Create a comparator which compares two beans by the specified bean
     * property. Property expression can use Apache's nested, indexed, combinated, mapped syntax. @see <a
     * href="http://commons.apache.org/beanutils/api/org/apache/commons/beanutils/BeanComparator.html">Apache's Bean
     * Comparator</a> for more details.
     * 
     * @param <T> generic type
     * @param propertyExpression propertyExpression
     * @return the comparator
     */
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> createPropertyComparator(final String propertyExpression) {
        return new BeanComparator(propertyExpression);
    }

    /**
     * Wrapping of Apache communs BeanComparator to avoid "unchecked" tag in code. Create a comparator which compares
     * two beans by the specified bean property. Property expression can use Apache's nested, indexed, combinated,
     * mapped syntax. @see <a
     * href="http://commons.apache.org/beanutils/api/org/apache/commons/beanutils/BeanComparator.html">Apache's Bean
     * Comparator</a> for more details.
     * 
     * @param <T> generic type
     * @param propertyExpression PropertyExpression
     * @param comparator BeanComparator will pass the values of the specified bean property to this Comparator. If your
     *            bean property is not a comparable or contains null values, a suitable comparator may be supplied in
     *            this constructor
     * @return the Comparator being used to compare beans
     */
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> createPropertyComparator(final String propertyExpression, Comparator<T> comparator) {
        return new BeanComparator(propertyExpression, comparator);
    }
}
