package com.numo.server.utils;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import static org.passay.WhitespaceRule.ERROR_CODE;

public class PasswordUtils {

    public static String generate() {
        final CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
        lowerCaseRule.setNumberOfCharacters(1);

        final CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
        upperCaseRule.setNumberOfCharacters(1);

        final CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
        digitRule.setNumberOfCharacters(1);

        final CharacterRule splCharRule = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        });
        splCharRule.setNumberOfCharacters(1);

        return new PasswordGenerator().generatePassword(10, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
    }
}
