package dwe.holding.vmas.controller;

import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.preferences.Template;
import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;


class IndexControllerTest {

    @Test
    void doTest() {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .build();
        String content =
                """
                               {"estimatedTime": 15, "insuranceCompany": "Proteq Polisnr:/Petplan Polisnr: ", "paymentMethod": "5", "firstPageMessage": "<div>\\r\\n<div style=\\"color: red; font-family: Verdana; font-size: 25px; text-align: center;\\">!! <u><strong>Aandachtspunt van de week</strong></u>!!</div>\\r\\n\\r\\n<p style=\\"text-align:center\\">&nbsp;</p>\\r\\n\\r\\n<h1 style=\\"text-align:center\\"><span style=\\"color:#008000\\"><span style=\\"font-size:36px\\"><span style=\\"font-family:comic sans ms,cursive\\">Prijzen Consulten zijn aangepast.</span></span></span></h1>\\r\\n\\r\\n<p>&nbsp;</p>\\r\\n\\r\\n<h1 style=\\"text-align:center\\">&nbsp;</h1>\\r\\n\\r\\n<p style=\\"text-align:center\\"><span style=\\"font-family:comic sans ms,cursive\\"><span style=\\"color:#FF0000\\">Niet alles op een dag (4 vaccinaties per dag) en smeer de afspraken uit over de weken!</span></span></p>\\r\\n\\r\\n<p style=\\"text-align:center\\">&nbsp;</p>\\r\\n\\r\\n<p style=\\"text-align:center\\">&nbsp;</p>\\r\\n</div>\\r\\n",
                                "room1": "Spreekkamer 1", "room2": "O.K.", "room3": "Balie", "room4": "", "roomAgenda": "R", "mandatoryExpireDate": "Y", "mandatoryConsultReason": "Y",
                                 "consultTextTemplate": "{\\"Templates\\": [{\\"Order\\" :1,\\"Title\\":\\"Ziek dier\\",\\"Text\\":\\"Zorgvraag:\\\\nAN:\\\\nAI: \\\\nAO:\\\\nAvO:\\\\nDdx:\\\\nPlan:\\\\nTx:\\\\nInfo aan eigenaar:\\\\nAfspraken:\\", \\"Selected\\" :true},{\\"Order\\" :2,\\"Title\\":\\"Vaccinatie\\",\\"Text\\":\\"AN: \\\\nVoeding:\\\\nMedicatie: j/n\\\\nVakantieplannen: j/n\\\\nAI:\\\\nAO:\\\\nEntstof:\\\\nOntworming: \\\\nInfo aan eigenaar: \\\\nAdviezen: \\\\nAfspraken: \\", \\"Selected\\" :false},{\\"Order\\" :3,\\"Title\\":\\"OK/Gebit\\",\\"Text\\":\\"Reden OK:\\\\nAN:\\\\nAI:\\\\nAO:\\\\nAvO:\\\\nNarcose / pijnstilling: \\\\nSamenvatting OK:\\\\nTx:\\\\nInfo aan eigenaar:\\\\nAfspraken:\\", \\"Selected\\" :false},{\\"Order\\" :4,\\"Title\\":\\"Ziek dier\\",\\"Text\\":\\"Zorgvraag:\\\\nAN:\\\\nAI: \\\\nAO:\\\\nAvO:\\\\nDdx:\\\\nPlan:\\\\nTx:\\\\nInfo aan eigenaar:\\\\nAfspraken:\\", \\"Selected\\" :false}]}",
                                  "openingstimes": "", "sendoutAppointmentReminderMail": "N"}
                        """;

        LocalMemberPreferences pref = mapper.readValue(content, LocalMemberPreferences.class);
        List<Template> a = pref.getConsultTextTemplate(mapper);
        pref.setConsultTextTemplate(mapper);
        String json = mapper.writeValueAsString(pref);
        System.out.println(json);
        pref = mapper.readValue(json, LocalMemberPreferences.class);
        a = pref.getConsultTextTemplate(mapper);
    }
}
