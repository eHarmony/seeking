package com.eharmony.matching.seeking.mapper;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class ProjectedResultMapperTest {
    
    private final ProjectedResultMapper mapper = new ProjectedResultMapper();
    
    public static class SimpleClass {
        private Integer id;
        private String name;
        private Date created;
        
        private SimpleClass() {
            setCreated(new Date());
        }
        
        public SimpleClass(Integer id, String name) {
            this();
            setId(id);
            setName(name);
        }

        public Integer getId() {
            return id;
        }

        private void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        public Date getCreated() {
            return created;
        }

        private void setCreated(Date created) {
            this.created = created;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((created == null) ? 0 : created.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SimpleClass other = (SimpleClass) obj;
            if (created == null) {
                if (other.created != null)
                    return false;
            } else if (!created.equals(other.created))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "SimpleClass [id=" + id + ", name=" + name + ", created="
                    + created + "]";
        }
    }
    private final SimpleClass s1 = new SimpleClass(100, "test name");
    
    private Object[] vals(Object... values) {
        return values;
    }
    
    private String[] props(String... properties) {
        return properties;
    }
    
    @Test
    public void mapTo_Simple() {
        SimpleClass s1 = new SimpleClass(100, "test name"); 
        
        String[] props = props("id", "name", "created");
        Object[] vals = vals(s1.getId(), s1.getName(), s1.getCreated());
        
        SimpleClass s2 = mapper.mapTo(SimpleClass.class, vals, props);
        assertEquals(s1, s2);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mapTo_Simple_notEnoughPropertyNames() {
        String[] props = props("id", "not enough property names");
        Object[] vals = vals(s1.getId(), s1.getName(), s1.getCreated());
        mapper.mapTo(SimpleClass.class, vals, props);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mapTo_Simple_tooManyPropertyNames() {
        String[] props = props("id", "too", "many", "property", "names");
        Object[] vals = vals(s1.getId(), s1.getName(), s1.getCreated());
        mapper.mapTo(SimpleClass.class, vals, props);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mapTo_Simple_badPropertyNames() {
        String[] props = props("id", "bad", "names");
        Object[] vals = vals(s1.getId(), s1.getName(), s1.getCreated());
        mapper.mapTo(SimpleClass.class, vals, props);
    }
    
    @Test
    public void mapTo_Integer() {
        Integer one = 1;
        assertEquals(one, mapper.mapTo(Integer.class, one, props("id")));    
    }
    
    @Test(expected = ClassCastException.class)
    public void mapTo_Integer_ClassCastException() {
        mapper.mapTo(Integer.class, vals("not a number"), props("id"));
    }
    
    @Test
    public void mapTo_String() {
        String testString = "test string";
        assertEquals(testString, mapper.mapTo(String.class, testString, props("name")));    
    }
}