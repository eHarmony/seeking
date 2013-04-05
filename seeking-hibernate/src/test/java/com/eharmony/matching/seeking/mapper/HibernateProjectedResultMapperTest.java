package com.eharmony.matching.seeking.mapper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.test.TestClass;

public class HibernateProjectedResultMapperTest {
    
    private final ProjectedResultMapper mapper = mock(ProjectedResultMapper.class);
    private final HibernateProjectedResultMapper hMapper = new HibernateProjectedResultMapper(mapper);
    
    private final String[] field = new String[] { "field" };
    private final String[] fields = new String[] { "id", "name" };
    
    private final Query<Integer,String> projected = QueryBuilder.builderFor(Integer.class, String.class, field).build();
    private final Query<Integer,TestClass> projected2 = QueryBuilder.builderFor(Integer.class, TestClass.class, fields).build();
    private final Query<String,String> unprojected = QueryBuilder.builderFor(String.class).build();
    
    final TestClass test = new TestClass(1L, "one", 0D, 0D);
    final String string = "test string";
    final List<String> strings = Arrays.asList("a","b","c","d");
    
    private final Object input = new Object();
    private final List<?> inputs = Arrays.asList(1,2,3,4,5);
    
    @After
    public void after() {
        verifyNoMoreInteractions(mapper);
    }
    
    @Test
    public void returnFields_single() {
        assertArrayEquals(field, hMapper.returnFields(projected));
    }

    @Test
    public void returnFields_multiple() {
        assertArrayEquals(fields, hMapper.returnFields(projected2));
    }
    
    @Test
    public void returnFields_none() {
        assertArrayEquals(new String[0], hMapper.returnFields(unprojected));
    }
    
    @Test
    public void mapResult_projected() {
        when(mapper.mapTo(String.class, input, field)).thenReturn(string);
        assertEquals(string, hMapper.mapResult(input, projected));
        verify(mapper).mapTo(String.class, input, field);
    }
    
    @Test
    public void mapResult_projected2() {
        when(mapper.mapTo(TestClass.class, input, fields)).thenReturn(test);
        assertEquals(test, hMapper.mapResult(input, projected2));
        verify(mapper).mapTo(TestClass.class, input, fields);
    }
    
    @Test
    public void mapResult_unprojected() {
        assertEquals(string, hMapper.mapResult(string, unprojected));
    }
    
    @Test
    public void mapResults_projected() {
        List<String> results = hMapper.mapResults(inputs, projected);
        // must iterarte the list since guava returns a lazily transformed collection
        for (String result : results) {
            // we didn't provide anything to the mock, must be null
            assertNull(result);
        }
        verify(mapper, times(inputs.size())).mapTo(eq(String.class), any(Object.class), eq(field));
    }
    
    @Test
    public void mapResults_projected2() {
        List<TestClass> results = hMapper.mapResults(inputs, projected2);
        // must iterarte the list since guava returns a lazily transformed collection
        for (TestClass result : results) {
            // we didn't provide anything to the mock, must be null
            assertNull(result);
        }
        verify(mapper, times(inputs.size())).mapTo(eq(TestClass.class), any(Object.class), eq(fields));
    }
    
    @Test
    public void mapResults_unprojected() {
        assertEquals(strings, hMapper.mapResults(strings, unprojected));
    }
}
