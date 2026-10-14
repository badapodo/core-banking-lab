package com.corebanking.lab.assignment10;

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
@Tag("assignment10")
class A10Step06Test extends FinancialLabSupport {
    @Test
    void stale_saga() throws Exception {
        coreFault(503); UUID id=UUID.fromString(saga(200,"stale").path("sagaId").asText()); awaitSaga(id,"RETRY_PENDING");
        try(Connection c=DriverManager.getConnection(dbUrl("orchestrator"),"orch","orch"); PreparedStatement s=c.prepareStatement("UPDATE sagas SET updated_at=now()-interval '2 hours' WHERE saga_id=?")) {
            s.setObject(1,id); s.executeUpdate();
        }
        restoreCoreNetwork(); expect(request(orchestrator,"POST","/api/v1/recovery/run",null,null),200); awaitSaga(id,"COMPLETED");
    }
}
