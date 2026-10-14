package com.corebanking.lab.assignment04;

import com.corebanking.lab.support.FinancialLabSupport;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.HttpResponse;
import java.sql.*;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Tag("assignment")
@Tag("assignment04")
class A04Step08Test extends FinancialLabSupport {
    @Test
    void deadlock_recovery() throws Exception {
        sql("CREATE SEQUENCE lab_deadlock_attempts");
        sql("CREATE FUNCTION lab_deadlock_once() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF nextval('lab_deadlock_attempts')=1 THEN RAISE EXCEPTION 'lab deadlock' USING ERRCODE='40P01'; END IF; RETURN NEW; END $$");
        sql("CREATE TRIGGER lab_deadlock BEFORE UPDATE ON accounts FOR EACH ROW EXECUTE FUNCTION lab_deadlock_once()");
        HttpResponse<String> first=transfer(200);
        if(first.statusCode()==201) committed(first);
        else { error(first,409,"RETRYABLE_CONFLICT"); committed(transfer(200)); }
        assertThat(balance(source)).isEqualTo(800); assertThat(balance(target)).isEqualTo(300);
        conserved(1100); reconciled();
    }
}
