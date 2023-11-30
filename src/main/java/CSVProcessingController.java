import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CSVProcessingController {

    private final CSVProcessingService csvProcessingService;

    public CSVProcessingController(CSVProcessingService csvProcessingService) {
        this.csvProcessingService = csvProcessingService;
    }

    @PostMapping("/processCSV")
    public void processCSVFiles(@RequestBody List<String> filePaths) {
        csvProcessingService.processCSVFiles(filePaths);
    }
}
