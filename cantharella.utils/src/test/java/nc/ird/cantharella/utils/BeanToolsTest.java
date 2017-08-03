/*
 * #%L
 * Cantharella :: Utils
 * $Id: BeanToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/BeanToolsTest.java $
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

import nc.ird.cantharella.utils.BeanTools;
import java.lang.reflect.Field;

import javax.annotation.Resource;

import nc.ird.cantharella.utils.BeanTools.AccessType;

import org.junit.Assert;
import org.junit.Test;

/**
 * BeanTools test
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class BeanToolsTest {

    /**
     * Bean for the test
     * 
     * @author acheype
     */
    public class Bean {

        /** integer */
        @Resource
        public Integer integer;

        /** string */
        public String string;

        /**
         * integer getter
         * 
         * @return integer
         */
        public Integer getInteger() {
            return integer;
        }

        /**
         * string getter
         * 
         * @return string
         */
        public String getString() {
            return string;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return BeanTools.hashCode(this, integer, string);
        }

        /**
         * integer setter
         * 
         * @param integer integer
         */
        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        /**
         * string setter
         * 
         * @param string string
         */
        public void setString(String string) {
            this.string = string;
        }
    }

    /**
     * Sub-bean for the test
     * 
     * @author acheype
     */
    public final class SubBean extends Bean {
        /** integer */
        @Resource
        public Integer subInteger;

        /**
         * subInteger getter
         * 
         * @return subInteger
         */
        public Integer getSubInteger() {
            return subInteger;
        }

        /**
         * subInteger setter
         * 
         * @param subInteger subInteger
         */
        public void setSubInteger(Integer subInteger) {
            this.subInteger = subInteger;
        }
    }

    /**
     * Bean for the test
     * 
     * @author acheype
     */
    public final class SubBean2 extends Bean {

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return 1;
        }
    }

    /** Bean test class 1 */
    public class Bean1 {

        /** name test 1 */
        private String name1;

        /** bean test class 2 **/
        private Bean2 bean2;

        /** id */
        public int id;

        /**
         * name1 getter
         * 
         * @return name1
         */
        public String getName1() {
            return name1;
        }

        /**
         * name1 setter
         * 
         * @param name1 name1
         */
        public void setName1(String name1) {
            this.name1 = name1;
        }

        /**
         * bean2 getter
         * 
         * @return bean2
         */
        public Bean2 getBean2() {
            return bean2;
        }

        /**
         * bean2 setter
         * 
         * @param bean2 bean2
         */
        public void setBean2(Bean2 bean2) {
            this.bean2 = bean2;
        }

        /**
         * id getter
         * 
         * @return id
         */
        public int getId() {
            return id;
        }

        /**
         * id setter
         * 
         * @param id id
         */
        public void setId(int id) {
            this.id = id;
        }
    }

    /** Bean test class 2 **/
    public class Bean2 {

        /** name test2 */
        public String name2;

        /**
         * name2 getter
         * 
         * @return name2
         */
        public String getName2() {
            return name2;
        }

        /**
         * name2 setter
         * 
         * @param name2 name2
         */
        public void setName2(String name2) {
            this.name2 = name2;
        }

    }

    /** Bean test class 2 **/
    public class Bean3 extends Bean1 {

        /** name test2 */
        public String name2;

        /** id **/
        public int id;

        /**
         * id getter
         * 
         * @return id
         */
        @Override
        public int getId() {
            return this.id;
        }

        /**
         * id setter
         * 
         * @param id id
         */
        @Override
        public void setId(int id) {
            this.id = id;
        }

        /**
         * name2 getter
         * 
         * @return name2
         */
        public String getName2() {
            return name2;
        }

        /**
         * name2 setter
         * 
         * @param name2 name2
         */
        public void setName2(String name2) {
            this.name2 = name2;
        }
    }

    /**
     * equals test
     * 
     * @throws NoSuchFieldException -
     * @throws SecurityException -
     */
    @Test
    public void beanEquals() throws SecurityException, NoSuchFieldException {

        Field fi = Bean.class.getField("integer");
        Field fs = Bean.class.getField("string");

        Bean bean = new Bean();
        Bean1 bean1 = new Bean1();
        bean1.setId(455);
        Bean3 bean3 = new Bean3();
        bean3.setId(455);
        // Field fx = Bean1.class.getField("id");
        // Field fz = Bean3.class.getField("id");

        Assert.assertTrue(BeanTools.equals(bean, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean, fi, fs));

        Assert.assertFalse(BeanTools.equals(bean, null, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, null, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, null, fi, fs));

        Assert.assertTrue(BeanTools.equals(bean, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean, fi, fs));

        Bean bean12 = new Bean();

        Assert.assertTrue(BeanTools.equals(bean, bean12, AccessType.GETTER, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean12, AccessType.FIELD, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean12, fi, fs));

        Assert.assertTrue(BeanTools.equals(bean12, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean12, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean12, bean, fi, fs));

        bean.setInteger(0);
        bean.setString("");

        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, fi, fs));

        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, fi, fs));

        bean12.setInteger(0);
        bean12.setString("");

        Assert.assertTrue(BeanTools.equals(bean, bean12, AccessType.GETTER, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean12, AccessType.FIELD, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean, bean12, fi, fs));

        Assert.assertTrue(BeanTools.equals(bean12, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean12, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertTrue(BeanTools.equals(bean12, bean, fi, fs));

        bean12.setInteger(1);

        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, fi, fs));

        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, fi, fs));

        bean12.setString("Toto");

        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, fi, fs));

        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, fi, fs));

        bean12.setInteger(0);

        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean, bean12, fi, fs));

        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.GETTER, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, AccessType.FIELD, "integer", "string"));
        Assert.assertFalse(BeanTools.equals(bean12, bean, fi, fs));

        Assert.assertTrue(BeanTools.equals(bean1, bean3, AccessType.GETTER, "id"));
        Assert.assertTrue(BeanTools.equals(bean1, bean3, AccessType.FIELD, "id"));
    }

    /**
     * hashCode test
     * 
     * @throws NoSuchFieldException -
     * @throws SecurityException -
     */
    @Test
    public void beanHashCode() throws SecurityException, NoSuchFieldException {
        Field fi = Bean.class.getField("integer");
        Field fs = Bean.class.getField("string");
        Bean bean1 = new Bean();
        Bean bean2 = new Bean();
        Assert.assertTrue(bean1.hashCode() == bean2.hashCode());
        Assert.assertTrue(BeanTools.hashCode(bean1, fi, fs) == BeanTools.hashCode(bean2, fi, fs));
        bean1.setInteger(0);
        bean1.setString("");
        Assert.assertFalse(bean1.hashCode() == bean2.hashCode());
        Assert.assertFalse(BeanTools.hashCode(bean1, fi, fs) == BeanTools.hashCode(bean2, fi, fs));
        bean2.setInteger(0);
        bean2.setString("");
        Assert.assertTrue(bean1.hashCode() == bean2.hashCode());
        Assert.assertTrue(BeanTools.hashCode(bean1, fi, fs) == BeanTools.hashCode(bean2, fi, fs));
        bean2.setInteger(1);
        Assert.assertFalse(bean1.hashCode() == bean2.hashCode());
        Assert.assertFalse(BeanTools.hashCode(bean1, fi, fs) == BeanTools.hashCode(bean2, fi, fs));
        bean2.setString("Toto");
        Assert.assertFalse(bean1.hashCode() == bean2.hashCode());
        Assert.assertFalse(BeanTools.hashCode(bean1, fi, fs) == BeanTools.hashCode(bean2, fi, fs));
        bean2.setInteger(0);
        Assert.assertFalse(bean1.hashCode() == bean2.hashCode());
        Assert.assertFalse(BeanTools.hashCode(bean1, fi, fs) == BeanTools.hashCode(bean2, fi, fs));
    }

    /**
     * toString test
     */
    @Test
    public void beanToString() {
        SubBean bean = new SubBean();
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: <null>\n- string: <null>\n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.GETTER, "integer", "string", "subInteger"));
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: <null>\n- string: <null>\n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.FIELD, "integer", "string", "subInteger"));
        bean.setInteger(0);
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: 0\n- string: <null>\n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.GETTER, "integer", "string", "subInteger"));
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: 0\n- string: <null>\n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.FIELD, "integer", "string", "subInteger"));
        bean.setString("");
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: 0\n- string: \n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.GETTER, "integer", "string", "subInteger"));
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: 0\n- string: \n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.FIELD, "integer", "string", "subInteger"));
        bean.setString("Toto");
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: 0\n- string: Toto\n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.GETTER, "integer", "string", "subInteger"));
        Assert.assertEquals(SubBean.class.getName() + "\n- integer: 0\n- string: Toto\n- subInteger: <null>",
                BeanTools.toString(bean, AccessType.FIELD, "integer", "string", "subInteger"));
    }

    /**
     * getAnnotatedField test KO
     * 
     * @throws SecurityException -
     * @throws NullPointerException -
     * @throws NoSuchFieldException -
     */
    @Test(expected = NoSuchFieldException.class)
    public void getAnnotatedPublicFieldKO() throws SecurityException, NullPointerException, NoSuchFieldException {
        BeanTools.getAnnotatedPublicField(new Bean(), Deprecated.class);
    }

    /**
     * getAnnotatedField test OK
     * 
     * @throws SecurityException -
     * @throws NullPointerException -
     * @throws NoSuchFieldException -
     */
    @Test
    public void getAnnotatedPublicFieldOK() throws SecurityException, NullPointerException, NoSuchFieldException {
        Field f = Bean.class.getField("integer");
        Assert.assertEquals(f, BeanTools.getAnnotatedPublicField(new Bean(), Resource.class));
    }

    /**
     * isAnnotationOnProperty test
     * 
     * @throws SecurityException -
     * @throws NullPointerException -
     * @throws NoSuchFieldException -
     */
    @Test
    public void isAnnotationOnProperty() throws SecurityException, NullPointerException, NoSuchFieldException {
        Assert.assertTrue(BeanTools.isAnnotationOnProperty(SubBean.class, Resource.class, "subInteger"));
        Assert.assertTrue(BeanTools.isAnnotationOnProperty(SubBean.class, Resource.class, "integer"));
        Assert.assertFalse(BeanTools.isAnnotationOnProperty(SubBean.class, Resource.class, "string"));
    }

    /**
     * isAnnotationOnProperty fail test
     * 
     * @throws SecurityException -
     * @throws NullPointerException -
     * @throws NoSuchFieldException -
     */
    @Test(expected = NoSuchFieldException.class)
    public void isAnnotationOnPropertyError() throws SecurityException, NullPointerException, NoSuchFieldException {
        BeanTools.isAnnotationOnProperty(SubBean.class, Resource.class, "toto");
    }

    /**
     * getValueFromPath Test
     */
    @Test(expected = IllegalArgumentException.class)
    public void getValueFromPath() {
        Bean1 b1 = new Bean1();
        Bean2 b2 = new Bean2();

        b1.setName1("name1");
        b2.setName2("name2");
        b1.setBean2(b2);

        Assert.assertSame(b1.getName1(), BeanTools.getValueFromPath(b1, AccessType.GETTER, "name1"));
        Assert.assertSame(b1.getBean2().getName2(), BeanTools.getValueFromPath(b1, AccessType.GETTER, "bean2.name2"));
        BeanTools.getValueFromPath(b1, AccessType.FIELD, "bean2.nameX");
    }

}
