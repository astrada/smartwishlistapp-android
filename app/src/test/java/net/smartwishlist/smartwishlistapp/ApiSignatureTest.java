package net.smartwishlist.smartwishlistapp;

import org.junit.Assert;
import org.junit.Test;

public class ApiSignatureTest {

    @Test
    public void testGenerateSignature() throws Exception {
        String signature = ApiSignature.generateRequestSignature(
                "Smart Wish List API v0.1 - Shared Secret", "test",
                1419607393.71);
        Assert.assertEquals("c5493a92b170a845a44cf78263a581f26ace023c91c18a1bc22b876807b827dd",
                signature);
    }

    @Test
    public void testGenerateSignatureApi() throws Exception {
        String signature = ApiSignature.generateRequestSignature(
                "14cf1e72acd716691c9e13a813b5551dfe761b5188189accf8f410f87299a6a2",
                "seagate 5tbCA",
                1420296642.96);
        Assert.assertEquals("f734253438a53261abc38981916a03528d987f08432a9899147aef231956e5b0",
                signature);
    }
}