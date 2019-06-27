package com.smarterspecies.magento2.api.tests;

import com.smarterspecies.magento2.api.payloads.tokenPayLoad.TokenPayLoad;
import com.smarterspecies.magento2.api.payloads.userPayLoad.Customer;
import com.smarterspecies.magento2.api.services.DeleteUserApiService;
import com.smarterspecies.magento2.api.services.TokenApiService;
import com.smarterspecies.magento2.api.services.UserApiService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;


public class DeleteUserTest extends BaseTest {

    private final Customer customer = new Customer();
    private final UserApiService userApiService = new UserApiService();
    private final DeleteUserApiService deleteUserApiService = new DeleteUserApiService();
    private final TokenPayLoad tokenPayLoad = new TokenPayLoad();
    private final TokenApiService tokenApiService = new TokenApiService();


    @Test
    public void testCanDeleteUser() {
        //given
        String randEmail = "automation_" + randomAlphanumeric(3) + "@gorillagroup.com";
        customer.setNewCustomerData(randEmail, FIRST_NAME, LAST_NAME, WEBSITE_ID, GROUP_ID, PASSWORD);
        tokenPayLoad.username(ADMIN).password(ADMIN_PASS);
        String token = tokenApiService.getAdminToken(tokenPayLoad);
        //when
        int userId = userApiService.registerNewUser(customer)
                .then()
                .assertThat().statusCode(200)
                .and()
                .extract().response().body().jsonPath().get("id");
        //then
        String result = deleteUserApiService.deleteUser(token, String.valueOf(userId))
                .then().assertThat().statusCode(200)
                .extract().response().asString();
        Assert.assertEquals(result, "true");

    }
    @Test
    public void testCanNotDeleteUserTwice() {
        //given
        String randEmail = "automation_" + randomAlphanumeric(3) + "@gorillagroup.com";
        customer.setNewCustomerData(randEmail, FIRST_NAME, LAST_NAME, WEBSITE_ID, GROUP_ID, PASSWORD);
        tokenPayLoad.username(ADMIN).password(ADMIN_PASS);
        String token = tokenApiService.getAdminToken(tokenPayLoad);
        //when
        int userId = userApiService.registerNewUser(customer)
                .then()
                .assertThat().statusCode(200)
                .and()
                .extract().response().body().jsonPath().get("id");
        //then
        String result = deleteUserApiService.deleteUser(token, String.valueOf(userId))
                .then().assertThat().statusCode(200)
                .extract().response().asString();
        Assert.assertEquals(result, "true");
        deleteUserApiService.deleteUser(token, String.valueOf(userId))
                .then().assertThat().statusCode(404)
                .and().body(not(isEmptyString()));
    }
}
