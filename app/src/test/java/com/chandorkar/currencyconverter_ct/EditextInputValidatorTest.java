package com.chandorkar.currencyconverter_ct;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EditextInputValidatorTest {

    @Test
    public void  editText_CorrectString_ReturnsTrue(){
        assertTrue(MainActivity.editTextValidator("1234"));
    }

    @Test
    public void  editText_EmptyString_ReturnsFalse(){
        assertFalse(MainActivity.editTextValidator.isValidValue(""));
    }

    @Test
    public void  editText_NullString_ReturnsFalse(){
        assertFalse(MainActivity.editTextValidator.isValidValue(null));
    }

}
