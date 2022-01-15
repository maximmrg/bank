package fr.miage.bank;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.User;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import fr.miage.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/data.sql"})
public class OperationTests {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    CarteService carteService;

    User userAdminTest;

    User userTest;
    Account accountTest;
    String basePath;
}
