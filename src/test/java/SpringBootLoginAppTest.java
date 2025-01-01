package com.example;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpringBootLoginAppTest {
    private static final int MAX_LENGTH = 255;
    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

   

    


    @Test
    void testContextLoads() {
        assertNotNull(mockMvc);
    }

   
    @ParameterizedTest
    @CsvSource({
        "testUser,testPassword,dashboard,''",
        "wrongUser,wrongPass,login,Invalid credentials",
        "testUser,wrongPass,login,Invalid credentials",
        "wrongUser,testPassword,login,Invalid credentials"
    })
    void testMultipleLoginScenarios(String username, String password, String expectedView, String expectedError) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/loginDuplicate")
                .param("username", username)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedView))
                .andExpect(expectedError.isEmpty() ? 
                    model().attributeDoesNotExist("error") : 
                    model().attribute("error", expectedError));

                    mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .param("username", username)
                    .param("password", password))
                    .andExpect(status().isOk())
                    .andExpect(view().name(expectedView))
                    .andExpect(expectedError.isEmpty() ? 
                        model().attributeDoesNotExist("error") : 
                        model().attribute("error", expectedError));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void testLoginWithEmptyAndWhitespaceCredentials(String input) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", input)
                .param("password", input))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));

                mockMvc.perform(MockMvcRequestBuilders.post("/loginDuplicate")
                .param("username", input)
                .param("password", input))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));
    }

    @Test
    void testLoginWithMaxLengthCredentials() throws Exception {
        String maxLengthString = "A".repeat(255);
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", maxLengthString)
                .param("password", maxLengthString))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));

    }

    @Test
    void testLoginWithSpecialCharacters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "test@User#$%!@#")
                .param("password", "test@Password#$%!@#"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));

                mockMvc.perform(MockMvcRequestBuilders.post("/loginDuplicate")
                .param("username", "test@User#$%!@#")
                .param("password", "test@Password#$%!@#"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));
    }

    @Test
    void testLoginWithUnicodeCharacters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "用户名")
                .param("password", "密码"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));

                mockMvc.perform(MockMvcRequestBuilders.post("/loginDuplicate")
                .param("username", "用户名")
                .param("password", "密码"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));
    }

    @Test
    void testLoginWithHTMLInjection() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "<script>alert('xss')</script>")
                .param("password", "<script>alert('xss')</script>"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));

                mockMvc.perform(MockMvcRequestBuilders.post("/loginDuplicate")
                .param("username", "<script>alert('xss')</script>")
                .param("password", "<script>alert('xss')</script>"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Invalid credentials"))
                .andExpect(view().name("login"));
    }

    @Test
    void testLoginWithDifferentHttpMethods() throws Exception {
        // Testing GET method (should fail)
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                .param("username", "testUser")
                .param("password", "testPassword"))
                .andExpect(status().isMethodNotAllowed());

                mockMvc.perform(MockMvcRequestBuilders.get("/loginDuplicate")
                .param("username", "testUser")
                .param("password", "testPassword"))
                .andExpect(status().isMethodNotAllowed());

        // Testing PUT method (should fail)
        mockMvc.perform(MockMvcRequestBuilders.put("/login")
                .param("username", "testUser")
                .param("password", "testPassword"))
                .andExpect(status().isMethodNotAllowed());
        
                mockMvc.perform(MockMvcRequestBuilders.put("/loginDuplicate")
                .param("username", "testUser")
                .param("password", "testPassword"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testLoginWithMissingParameters() throws Exception {
        // Test with missing username
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("password", "testPassword"))
                .andExpect(status().isBadRequest());

        // Test with missing password
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "testUser"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSuccessfulLoginModelAttributes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "testUser")
                .param("password", "testPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void testLoginWithNullValues() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", null);
        params.add("password", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .params(params))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testNullInputValidation() throws Exception {
        // Testing null username and password handling
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", (String) null)
                .param("password", (String) null))
                .andExpect(status().isBadRequest())  // Expect 400 Bad Request
                .andExpect(model().attribute("error", "Missing required parameters"));
    
        // Test empty string cases instead of null
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "")
                .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "Invalid credentials"));
    
        // Test one null parameter
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "testUser"))  // password parameter missing
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("error", "Missing required parameters"));
    
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("password", "testPass"))  // username parameter missing
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("error", "Missing required parameters"));
    }
    
    // Add a separate test for empty strings to ensure coverage
    @Test
    void testEmptyStringValidation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "")
                .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "Invalid credentials"));
    }
    
    // Add a test for actual null object values
    @Test
    void testNullObjectValidation() throws Exception {
        String nullString = null;
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", nullString)
                .param("password", nullString))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("error", "Missing required parameters"));
    }


@Test
void testMaxLengthValidation() throws Exception {
    String longInput = "A".repeat(MAX_LENGTH + 1);
    
    // Test max length validation for both username and password
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", longInput)
            .param("password", longInput))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Invalid credentials"));
    
    // Test max length validation for username only
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", longInput)
            .param("password", "normalPassword"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Invalid credentials"));
    
    // Test max length validation for password only
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", "normalUsername")
            .param("password", longInput))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Invalid credentials"));
}

@Test
void testEmptyAndWhitespaceValidation() throws Exception {
    // Test empty strings
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", "")
            .param("password", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Invalid credentials"));
    
    // Test whitespace-only strings
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", "   ")
            .param("password", "   "))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Invalid credentials"));
    
    // Test one empty, one whitespace
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", "")
            .param("password", "   "))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Invalid credentials"));
}
}