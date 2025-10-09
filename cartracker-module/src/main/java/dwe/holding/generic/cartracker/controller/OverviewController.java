package dwe.holding.generic.cartracker.controller;

import dwe.holding.generic.cartracker.model.Trip;
import dwe.holding.generic.cartracker.service.CarService;
import dwe.holding.generic.cartracker.service.DriveService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
class OverviewController {

    final DriveService driveService;
    final CarService carService;
    String startTable = """
            <details class="collapsable-table">
            <summary>Click to open or close</summary>
            <table class="table-tight %s" id="table">
            """;
    String headerTable = """
            <thead>
            <tr><td>Daan</td><td>Suus</td><td>Maria&nbsp;&nbsp;&nbsp;</td><td>Tot Km</td><td>Ltrs</td><td>€</td><td>Paid</td></tr>
            </thead>
            """;
    String dataRowTable = """
            <tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>
            """;
    String footerTable = """
            <tfoot></tfoot></table></details>
            """;
    DecimalFormat df = new DecimalFormat("##.##");

    OverviewController(DriveService driveService, CarService carService) {
        this.driveService = driveService;
        this.carService = carService;
    }

    @GetMapping("/cartracker/trip/alluser/tank")
    public String getCarListTank(Model model) {

        List<Trip> list = driveService.getAllAsList();
        StringBuffer sb = new StringBuffer();

        int totalT_Daniel = 0, totalT_Maria = 0, totalT_Suzanne = 0;
        int totalVW_Daniel = 0, totalVW_Maria = 0, totalVW_Suzanne = 0;
        int oddOreven = 0;

        for (Trip trip : list) {
            if (trip.getCarType().equalsIgnoreCase("Toyota")) {

                if (trip.getPerson().equalsIgnoreCase("daniel")) {
                    totalT_Daniel += trip.getKm();
                }
                if (trip.getPerson().equalsIgnoreCase("maria")) {
                    totalT_Maria += trip.getKm();
                }
                if (trip.getPerson().equalsIgnoreCase("suzanne")) {
                    totalT_Suzanne += trip.getKm();
                }
            }

            if (trip.getCarType().equalsIgnoreCase("VW")) {
                if (trip.getPerson().equalsIgnoreCase("daniel")) {
                    totalVW_Daniel += trip.getKm();
                }
                if (trip.getPerson().equalsIgnoreCase("maria")) {
                    totalVW_Maria += trip.getKm();
                }
                if (trip.getPerson().equalsIgnoreCase("suzanne")) {
                    totalVW_Suzanne += trip.getKm();
                }
            }

            if (trip.getLiters() != 0) {
                sb.append(startTable.formatted((oddOreven++ % 2 == 0) ? "odd" : "even"));
                sb.append(headerTable);
                sb.append("<tbody>");
                double totalAmount = trip.getAmount() * 1.0 / 100;

                if (trip.getCarType().equalsIgnoreCase("VW")) {
                    int totalKms = totalVW_Daniel + totalVW_Suzanne + totalVW_Maria;

                    sb.append(dataRowTable.formatted(totalVW_Daniel, totalVW_Suzanne, totalVW_Maria, totalKms,
                                    trip.getLiters(),
                                    "€" + df.format(totalAmount),
                                    "<b>" + trip.getPerson() + "</b>"
                            )
                    );

                    extracted(sb,
                            totalVW_Daniel, (totalVW_Daniel * 1.0 / totalKms * 100),
                            totalVW_Suzanne, (totalVW_Suzanne * 1.0 / totalKms * 100),
                            totalVW_Maria, (totalVW_Maria * 1.0 / totalKms * 100),
                            totalAmount);

                    totalVW_Daniel = totalVW_Maria = totalVW_Suzanne = 0;
                }
                if (trip.getCarType().equalsIgnoreCase("Toyota")) {
                    int totalKms = totalT_Daniel + totalT_Suzanne + totalT_Maria;

                    sb.append(dataRowTable.formatted(totalT_Daniel, totalT_Suzanne, totalT_Maria, totalKms,
                                    trip.getLiters(),
                                    "€" + df.format(totalAmount),
                                    "<b>" + trip.getPerson() + "</b>"
                            )
                    );

                    extracted(sb, totalT_Daniel, (totalT_Daniel * 1.0 / totalKms * 100),
                            totalT_Suzanne, (totalT_Suzanne * 1.0 / totalKms * 100),
                            totalT_Maria, (totalT_Maria * 1.0 / totalKms * 100),
                            totalAmount);

                    totalT_Daniel = totalT_Suzanne = totalT_Maria = 0;
                    sb.append(dataRowTable.formatted("", "", "", "", "", "", ""));
                }
                sb.append("</tbody>");
                sb.append(footerTable);
            }
        }
        sb.append(startTable.formatted((oddOreven % 2 == 0) ? "odd" : "even"));
        sb.append(headerTable);
        sb.append(dataRowTable.formatted("left over:", "", "", "", "", "", ""));
        sb.append(dataRowTable.formatted(totalVW_Daniel, totalVW_Suzanne, totalVW_Maria,"", "VW", "", ""));
        sb.append(dataRowTable.formatted(totalT_Daniel, totalT_Suzanne, totalT_Maria,"", "Toyota", "", ""));
        sb.append("</tbody>");
        sb.append(footerTable);

        model.addAttribute("fueloverview", sb.append(footerTable));
        return "cartracker-module/overview";
    }

    private void extracted(StringBuffer sb, int total_Daniel, double percDaniel, int total_Suzanne, double percSuzanne, int totalT_Maria, double percMaria, double totalAmount) {
        sb.append(dataRowTable.formatted(
                        (total_Daniel == 0) ? "0 %" : df.format(percDaniel) + "%",
                        (total_Suzanne == 0) ? "0 %" : df.format(percSuzanne) + "%",
                        (totalT_Maria == 0) ? "0 %" : df.format(percMaria) + "%",
                        "", "%", "VW", ""
                )
        );
        sb.append(dataRowTable.formatted(
                        (total_Daniel == 0) ? "€0" : "€" + df.format(totalAmount * percDaniel / 100),
                        (total_Suzanne == 0) ? "€0" : "€" + df.format(totalAmount * percSuzanne / 100),
                        (totalT_Maria == 0) ? "€0" : "€" + df.format(totalAmount * percMaria / 100),
                        "", "fuel", "VW", ""
                )
        );
        sb.append(dataRowTable.formatted(
                        (total_Daniel == 0) ? "€0" : "€" + df.format(total_Daniel * 0.1),
                        (total_Suzanne == 0) ? "€0" : "€" + df.format(total_Suzanne * 0.1),
                        (totalT_Maria == 0) ? "€0" : "€" + df.format(totalT_Maria * 0.1),
                        "", "cost", "VW", ""
                )
        );
    }

    @GetMapping("/cartracker/trip/alluser")
    public String getCarList(Model model) {
        List<Trip> list = driveService.getAllAsList();

        model.addAttribute("trips", list);
        model.addAttribute("kmTotal",
                list.stream()
                        .map(Trip::getKm).map(Long::valueOf)
                        .reduce(0L, Long::sum)
        );
        model.addAttribute("litersTotal",
                list.stream()
                        .map(Trip::getLiters).map(Long::valueOf)
                        .reduce(0L, Long::sum)
        );
        return "cartracker-module/listtrips";
    }

    @GetMapping("/cartracker/trip/all")
    public ResponseEntity<String> getCarRecordList() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/csv");
        responseHeaders.add("Content-Disposition", "attachment; filename=trips.csv");
        return new ResponseEntity<>(
                driveService.getAllAsCsv().stream().reduce((a, b) -> a + "\r\n" + b).get(),
                responseHeaders,
                HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleNotFound(IllegalArgumentException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}