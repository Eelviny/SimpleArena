package co.justgame.simpleArena.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.server.v1_7_R3.SharedConstants;
 
public class SpecialChars {
 
    public static void ModifyAllowedCharacters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        Field field = SharedConstants.class.getDeclaredField("allowedCharacters");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField( "modifiers" );
        modifiersField.setAccessible( true );
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        String oldallowedchars = new String((char[]) field.get(null));
        String suits = "\u25A0\u00A7";
        String newvalue = oldallowedchars + suits;
        field.set( null, newvalue.toCharArray());
    }
 
}