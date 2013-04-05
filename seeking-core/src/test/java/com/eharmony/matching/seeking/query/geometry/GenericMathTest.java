package com.eharmony.matching.seeking.query.geometry;

import static com.eharmony.matching.seeking.query.geometry.GenericMath.add;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.bigDec;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.bigInt;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.divide;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.max;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.min;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.multiply;
import static com.eharmony.matching.seeking.query.geometry.GenericMath.subtract;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class GenericMathTest {
    
    @Test
    public void add_Integer() {
        Integer sum = 5;
        Integer a = 3;
        assertEquals(sum, add(a, 2));
        assertEquals(sum, add(a, 2L));
        assertEquals(sum, add(a, 2.0F));
        assertEquals(sum, add(a, 2.0D));
        assertEquals(sum, add(a, bigInt(2)));
        assertEquals(sum, add(a, bigDec(2)));
    }
    
    @Test
    public void add_Long() {
        Long sum = 3000000000L;
        Long a = 2147483647L;
        assertEquals(sum, add(a, 852516353));
        assertEquals(sum, add(a, 852516353L));
        assertEquals(sum, add(a, 852516353.0D));
        assertEquals(sum, add(a, bigInt(852516353)));
        assertEquals(sum, add(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(Long.valueOf(5), add(3L, 2.0F));
    }
    
    @Test
    public void add_Float() {
        Float sum = 5.0F;
        Float a = 3.0F;
        assertEquals(sum, add(a, 2));
        assertEquals(sum, add(a, 2L));
        assertEquals(sum, add(a, 2.0F));
        assertEquals(sum, add(a, 2.0D));
        assertEquals(sum, add(a, bigInt(2)));
        assertEquals(sum, add(a, bigDec(2)));
    }

    @Test
    public void add_Double() {
        Double sum = 3000000000D;
        Double a = 2147483647D;
        assertEquals(sum, add(a, 852516353));
        assertEquals(sum, add(a, 852516353L));
        assertEquals(sum, add(a, 852516353.0D));
        assertEquals(sum, add(a, bigInt(852516353)));
        assertEquals(sum, add(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(Double.valueOf(5), add(3D, 2.0F));
    }
    
    @Test
    public void add_BigInteger() {
        BigInteger sum = bigInt(3000000000L);
        BigInteger a = bigInt(2147483647L);
        assertEquals(sum, add(a, 852516353));
        assertEquals(sum, add(a, 852516353L));
        assertEquals(sum, add(a, 852516353.0D));
        assertEquals(sum, add(a, bigInt(852516353)));
        assertEquals(sum, add(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(bigInt(5), add(bigInt(3), 2.0F));
    }
    
    @Test
    public void add_BigDecimal() {
        BigDecimal sum = bigDec(3000000000D);
        BigDecimal a = bigDec(2147483647D);
        assertEquals(sum, add(a, 852516353));
        assertEquals(sum, add(a, 852516353L));
        assertEquals(sum, add(a, 852516353.0D));
        assertEquals(sum, add(a, bigInt(852516353)));
        assertEquals(sum, add(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(bigDec(5), add(bigDec(3), 2.0F));
    }
    
    @Test
    public void subtract_Integer() {
        Integer a = 5;
        Integer sum = 3;
        assertEquals(sum, subtract(a, 2));
        assertEquals(sum, subtract(a, 2L));
        assertEquals(sum, subtract(a, 2.0F));
        assertEquals(sum, subtract(a, 2.0D));
        assertEquals(sum, subtract(a, bigInt(2)));
        assertEquals(sum, subtract(a, bigDec(2)));
    }
    
    @Test
    public void subtract_Long() {
        Long a = 3000000000L;
        Long sum = 2147483647L;
        assertEquals(sum, subtract(a, 852516353));
        assertEquals(sum, subtract(a, 852516353L));
        assertEquals(sum, subtract(a, 852516353.0D));
        assertEquals(sum, subtract(a, bigInt(852516353)));
        assertEquals(sum, subtract(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(Long.valueOf(3), subtract(5L, 2.0F));
    }
    
    @Test
    public void subtract_Float() {
        Float a = 5.0F;
        Float sum = 3.0F;
        assertEquals(sum, subtract(a, 2));
        assertEquals(sum, subtract(a, 2L));
        assertEquals(sum, subtract(a, 2.0F));
        assertEquals(sum, subtract(a, 2.0D));
        assertEquals(sum, subtract(a, bigInt(2)));
        assertEquals(sum, subtract(a, bigDec(2)));
    }

    @Test
    public void subtract_Double() {
        Double a = 3000000000D;
        Double sum = 2147483647D;
        assertEquals(sum, subtract(a, 852516353));
        assertEquals(sum, subtract(a, 852516353L));
        assertEquals(sum, subtract(a, 852516353.0D));
        assertEquals(sum, subtract(a, bigInt(852516353)));
        assertEquals(sum, subtract(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(Double.valueOf(3), subtract(5D, 2.0F));
    }
    
    @Test
    public void subtract_BigInteger() {
        BigInteger a = bigInt(3000000000L);
        BigInteger sum = bigInt(2147483647L);
        assertEquals(sum, subtract(a, 852516353));
        assertEquals(sum, subtract(a, 852516353L));
        assertEquals(sum, subtract(a, 852516353.0D));
        assertEquals(sum, subtract(a, bigInt(852516353)));
        assertEquals(sum, subtract(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(bigInt(3), subtract(bigInt(5), 2.0F));
    }
    
    @Test
    public void subtract_BigDecimal() {
        BigDecimal a = bigDec(3000000000D);
        BigDecimal sum = bigDec(2147483647D);
        assertEquals(sum, subtract(a, 852516353));
        assertEquals(sum, subtract(a, 852516353L));
        assertEquals(sum, subtract(a, 852516353.0D));
        assertEquals(sum, subtract(a, bigInt(852516353)));
        assertEquals(sum, subtract(a, bigDec(852516353)));
        // special case for float because the precision is bad
        assertEquals(bigDec(3), subtract(bigDec(5), 2.0F));
    }
    
    @Test
    public void multiply_Integer() {
        Integer sum = 6;
        Integer a = 3;
        assertEquals(sum, multiply(a, 2));
        assertEquals(sum, multiply(a, 2L));
        assertEquals(sum, multiply(a, 2.0F));
        assertEquals(sum, multiply(a, 2.0D));
        assertEquals(sum, multiply(a, bigInt(2)));
        assertEquals(sum, multiply(a, bigDec(2)));
    }
    
    @Test
    public void multiply_Long() {
        Long sum = 4294967294L;
        Long a = 2147483647L;
        assertEquals(sum, multiply(a, 2));
        assertEquals(sum, multiply(a, 2L));
        assertEquals(sum, multiply(a, 2.0D));
        assertEquals(sum, multiply(a, bigInt(2)));
        assertEquals(sum, multiply(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(Long.valueOf(6), multiply(3L, 2.0F));
    }
    
    @Test
    public void multiply_Float() {
        Float sum = 6.0F;
        Float a = 3.0F;
        assertEquals(sum, multiply(a, 2));
        assertEquals(sum, multiply(a, 2L));
        assertEquals(sum, multiply(a, 2.0F));
        assertEquals(sum, multiply(a, 2.0D));
        assertEquals(sum, multiply(a, bigInt(2)));
        assertEquals(sum, multiply(a, bigDec(2)));
    }

    @Test
    public void multiply_Double() {
        Double sum = 4294967294D;
        Double a = 2147483647D;
        assertEquals(sum, multiply(a, 2));
        assertEquals(sum, multiply(a, 2L));
        assertEquals(sum, multiply(a, 2.0D));
        assertEquals(sum, multiply(a, bigInt(2)));
        assertEquals(sum, multiply(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(Double.valueOf(6), multiply(3D, 2.0F));
    }
    
    @Test
    public void multiply_BigInteger() {
        BigInteger sum = bigInt(4294967294L);
        BigInteger a = bigInt(2147483647L);
        assertEquals(sum, multiply(a, 2));
        assertEquals(sum, multiply(a, 2L));
        assertEquals(sum, multiply(a, 2.0D));
        assertEquals(sum, multiply(a, bigInt(2)));
        assertEquals(sum, multiply(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(bigInt(6), multiply(bigInt(3), 2.0F));
    }
    
    @Test
    public void multiply_BigDecimal() {
        BigDecimal sum = bigDec(4294967294D);
        BigDecimal a = bigDec(2147483647D);
        assertEquals(sum, multiply(a, 2));
        assertEquals(sum, multiply(a, 2L));
        assertEquals(sum, multiply(a, 2.0D));
        assertEquals(sum, multiply(a, bigInt(2)));
        assertEquals(sum, multiply(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(bigDec(6), multiply(bigDec(3), 2.0F));
    }
    
    @Test
    public void divide_Integer() {
        Integer a = 6;
        Integer sum = 3;
        assertEquals(sum, divide(a, 2));
        assertEquals(sum, divide(a, 2L));
        assertEquals(sum, divide(a, 2.0F));
        assertEquals(sum, divide(a, 2.0D));
        assertEquals(sum, divide(a, bigInt(2)));
        assertEquals(sum, divide(a, bigDec(2)));
    }
    
    @Test
    public void divide_Long() {
        Long a= 4294967294L;
        Long sum = 2147483647L;
        assertEquals(sum, divide(a, 2));
        assertEquals(sum, divide(a, 2L));
        assertEquals(sum, divide(a, 2.0D));
        assertEquals(sum, divide(a, bigInt(2)));
        assertEquals(sum, divide(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(Long.valueOf(3), divide(6L, 2.0F));
    }
    
    @Test
    public void divide_Float() {
        Float a = 6.0F;
        Float sum = 3.0F;
        assertEquals(sum, divide(a, 2));
        assertEquals(sum, divide(a, 2L));
        assertEquals(sum, divide(a, 2.0F));
        assertEquals(sum, divide(a, 2.0D));
        assertEquals(sum, divide(a, bigInt(2)));
        assertEquals(sum, divide(a, bigDec(2)));
    }

    @Test
    public void divide_Double() {
        Double a = 4294967294D;
        Double sum = 2147483647D;
        assertEquals(sum, divide(a, 2));
        assertEquals(sum, divide(a, 2L));
        assertEquals(sum, divide(a, 2.0D));
        assertEquals(sum, divide(a, bigInt(2)));
        assertEquals(sum, divide(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(Double.valueOf(3), divide(6D, 2.0F));
    }
    
    @Test
    public void divide_BigInteger() {
        BigInteger a = bigInt(4294967294L);
        BigInteger sum = bigInt(2147483647L);
        assertEquals(sum, divide(a, 2));
        assertEquals(sum, divide(a, 2L));
        assertEquals(sum, divide(a, 2.0D));
        assertEquals(sum, divide(a, bigInt(2)));
        assertEquals(sum, divide(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(bigInt(3), divide(bigInt(6), 2.0F));
    }
    
    @Test
    public void divide_BigDecimal() {
        BigDecimal a = bigDec(4294967294D);
        BigDecimal sum = bigDec(2147483647D);
        assertEquals(sum, divide(a, 2));
        assertEquals(sum, divide(a, 2L));
        assertEquals(sum, divide(a, 2.0D));
        assertEquals(sum, divide(a, bigInt(2)));
        assertEquals(sum, divide(a, bigDec(2)));
        // special case for float because the precision is bad
        assertEquals(bigDec(3), divide(bigDec(6), 2.0F));
    }
    
    @Test
    public void  min_Integer() {
        Integer a = 42;
        Integer b = 100;
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_Long() {
        Long a = 42L;
        Long b = 100L;
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_Float() {
        Float a = 42F;
        Float b = 100F;
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_Double() {
        Double a = 42D;
        Double b = 100D;
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_BigInteger() {
        BigInteger a = bigInt(42);
        BigInteger b = bigInt(100);
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_BigDecimal() {
        BigDecimal a = bigDec(42);
        BigDecimal b = bigDec(100);
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_String() {
        String a = "a";
        String b = "b";
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  min_Date() {
        Calendar c = Calendar.getInstance();
        Date a = new Date(c.getTime().getTime());
        c.add(Calendar.DATE, 1);
        Date b = new Date(c.getTime().getTime());
        assertEquals(a, min(a, b));
        assertEquals(a, min(b, a));
    }
    
    @Test
    public void  max_Integer() {
        Integer a = 42;
        Integer b = 100;
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_Long() {
        Long a = 42L;
        Long b = 100L;
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_Float() {
        Float a = 42F;
        Float b = 100F;
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_Double() {
        Double a = 42D;
        Double b = 100D;
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_BigInteger() {
        BigInteger a = bigInt(42);
        BigInteger b = bigInt(100);
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_BigDecimal() {
        BigDecimal a = bigDec(42);
        BigDecimal b = bigDec(100);
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_String() {
        String a = "a";
        String b = "b";
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
    
    @Test
    public void  max_Date() {
        Calendar c = Calendar.getInstance();
        Date a = new Date(c.getTime().getTime());
        c.add(Calendar.DATE, 1);
        Date b = new Date(c.getTime().getTime());
        assertEquals(b, max(a, b));
        assertEquals(b, max(b, a));
    }
}
