package net.risesoft.y9.util.signing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import net.risesoft.consts.PasswordHashAlgorithmConsts;

class Y9PasswordMatchTest {

    private static final String HASH = Y9MessageDigestUtil.bcrypt("Chosen1!");

    @Test
    void bcryptVerifiesCorrectAndIncorrectPasswords() {
        assertTrue(Y9MessageDigestUtil.passwordMatch("Chosen1!", HASH, PasswordHashAlgorithmConsts.BCRYPT));
        assertTrue(Y9MessageDigestUtil.passwordMatch("Chosen1!", HASH,
            PasswordHashAlgorithmConsts.BCRYPT.toUpperCase(Locale.ROOT)));
        assertFalse(Y9MessageDigestUtil.passwordMatch("incorrect", HASH, PasswordHashAlgorithmConsts.BCRYPT));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown", "sha256", " "})
    void missingOrUnsupportedAlgorithmDoesNotFallBackToBcrypt(String algorithm) {
        assertFalse(Y9MessageDigestUtil.passwordMatch("Chosen1!", HASH, algorithm));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"plaintext", "$2a$10$bad"})
    void malformedHashesFailWithoutThrowing(String hash) {
        assertFalse(Y9MessageDigestUtil.passwordMatch("Chosen1!", hash, PasswordHashAlgorithmConsts.BCRYPT));
    }

    @Test
    void nullPasswordDoesNotMatch() {
        assertFalse(Y9MessageDigestUtil.passwordMatch(null, HASH, PasswordHashAlgorithmConsts.BCRYPT));
    }
}
