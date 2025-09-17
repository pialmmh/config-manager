package freeswitch.service;

import freeswitch.dto.DidAssignmentDto;
import com.telcobright.rtc.domainmodel.mysqlentity.DidAssignment;
import com.telcobright.rtc.domainmodel.mysqlentity.DidNumber;
import com.telcobright.rtc.domainmodel.mysqlentity.Partner;
import freeswitch.repository.mysqlrepository.DidAssignmentRepository;
import freeswitch.repository.mysqlrepository.DidNumberRepository;
import freeswitch.repository.mysqlrepository.PartnerRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DidAssignmentService extends GenericCrudService<DidAssignment, Integer> {
    private final DidAssignmentRepository didAssignmentRepository;
    private final PartnerRepository partnerRepository;
    private final DidNumberRepository didNumberRepository;

    public DidAssignmentService(DidAssignmentRepository didAssignmentRepository, PartnerRepository partnerRepository, DidNumberRepository didNumberRepository) {
        super(didAssignmentRepository);
        this.didAssignmentRepository = didAssignmentRepository;
        this.partnerRepository = partnerRepository;
        this.didNumberRepository = didNumberRepository;
    }

    public ResponseEntity<DidAssignment> createDidAssignment(DidAssignmentDto didAssignmentDto) {
        try{
            Partner dbPartner = partnerRepository.findById(didAssignmentDto.getIdPartner()).orElseThrow(()->new Exception("Could not find partner"));
            DidNumber dbDidNumber = didNumberRepository.findById(didAssignmentDto.getDidNumberId()).orElseThrow(()->new Exception("Could not find did number"));
            DidAssignment didAssignment = new DidAssignment(
                    dbPartner.getIdPartner(),
                    dbDidNumber.getId(),
                    didAssignmentDto.getStartDate(),
                    didAssignmentDto.getExpiryDate(),
                    didAssignmentDto.getDescription()
            );
            DidAssignment savedDidAssignment = didAssignmentRepository.save(didAssignment);
            dbPartner.getDidAssignments().add(savedDidAssignment);

            return new ResponseEntity<>(savedDidAssignment, HttpStatus.CREATED);  //return 201 Created status code if successful

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //return createEntity(didAssignment);

    }

    public ResponseEntity<DidAssignment> updateDidAssignment(DidAssignmentDto didAssignment) {
        try {
            DidAssignment existingDidAssignment = didAssignmentRepository.findByDidNumberId(didAssignment.getDidNumberId()).orElseThrow(() -> new ExpressionException("Did assignment not found"));
            //existingDidAssignment.setPartner(partnerRepository.findById(didAssignment.getIdPartner()).orElseThrow(() -> new ExpressionException("Could not find partner")));
            //existingDidAssignment.setDidNumber(didNumberRepository.findById(didAssignment.getDidNumberId()).orElseThrow(() -> new ExpressionException("Could not find did number")));
            existingDidAssignment.setStartDate(didAssignment.getStartDate());
            existingDidAssignment.setExpiryDate(didAssignment.getExpiryDate());
            existingDidAssignment.setDescription(didAssignment.getDescription());
            //existingDidAssignment.setDidPool(didPoolRepository.findById(didAssignment.getDidPoolId()).orElseThrow(() -> new ExpressionException("Could not find did pool")));

            return new ResponseEntity<>(didAssignmentRepository.save(existingDidAssignment), HttpStatus.OK);  //return 200 OK status code if successful

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteDidAssignment(Integer id) {
        return deleteEntity(id);
    }

    public ResponseEntity<DidAssignmentDto> getDidAssignment(Integer id) {
        try{
            DidAssignment didAssignment = didAssignmentRepository.findById(id).orElseThrow(() -> new ExpressionException("Did assignment not found"));
            Partner partner = partnerRepository.findById(didAssignment.getIdPartner()).orElseThrow(() -> new Exception("could not find partner"));
            return new ResponseEntity<>(new DidAssignmentDto(
                    didAssignment.getDidNumberId(),
                    didAssignment.getIdPartner(),
                    partner.getPartnerName(),
                    didAssignment.getStartDate(),
                    didAssignment.getExpiryDate(),
                    didAssignment.getDescription()
            ), HttpStatus.OK);  //return 200 OK status code if successful

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<DidAssignment>> getDidAssignments() {
        return getEntities();
    }

    public ResponseEntity<String> createDidAssignmentsFromCsv(MultipartFile file) {
        try{
            if (file.isEmpty()) throw new Exception("File is empty!");
            String fileName = file.getOriginalFilename();
            List<DidAssignmentDto> didAssignments = parseCsvFile(file);
            if (fileName.endsWith(".csv")) {
                didAssignments = parseCsvFile(file);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                didAssignments = parseExcelFile(file);
            } else {
                return ResponseEntity.badRequest().body("Unsupported file type. Please upload a CSV or Excel file.");
            }

            for (DidAssignmentDto didAssignmentDto : didAssignments) {
                createDidAssignment(didAssignmentDto);
            }
            return ResponseEntity.ok("Ok");
        }catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<DidAssignmentDto> parseCsvFile(MultipartFile file) throws Exception {
        List<DidAssignmentDto> didAssignments = new ArrayList<>();

        // Create a BufferedReader to read the CSV file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean firstLine = true;

            // Read each line from the CSV
            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Split the line by commas
                String[] fields = line.split(",");

                // Check if the number of fields is correct
                if (fields.length != 6) {
                    System.out.println("Skipping invalid row: " + line);
                    continue;
                }

                // Create a new DidAssignmentDto object and set the values from the CSV
                DidAssignmentDto didAssignmentDto = new DidAssignmentDto();
                didAssignmentDto.setDidNumberId('0'+ fields[0].trim());  // Remove any extra spaces
                didAssignmentDto.setIdPartner(Integer.parseInt(fields[1].trim()));
                didAssignmentDto.setStartDate(parseDate(fields[2].trim()));
                didAssignmentDto.setExpiryDate(parseDate(fields[3].trim()));
                didAssignmentDto.setDescription(fields[4].trim());

                // Add the DidAssignmentDto to the list
                didAssignments.add(didAssignmentDto);
            }
        }
        return didAssignments;
    }

    private List<DidAssignmentDto> parseExcelFile(MultipartFile file) throws Exception {
        List<DidAssignmentDto> didAssignments = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over rows, starting from the second row (skip header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row != null) {
                    DidAssignmentDto didAssignmentDto = new DidAssignmentDto();

                    // Column 0: did_number (as String)
                    Cell didNumberCell = row.getCell(0);
                    if (didNumberCell != null) {
                        if (didNumberCell.getCellType() == CellType.STRING) {
                            didAssignmentDto.setDidNumberId("0" + didNumberCell.getStringCellValue().trim());
                        } else if (didNumberCell.getCellType() == CellType.NUMERIC) {
                            didAssignmentDto.setDidNumberId("0" + String.valueOf((long) didNumberCell.getNumericCellValue()).trim());
                        }
                    }

                    // Column 1: partner_ic (as Integer)
                    Cell partnerCell = row.getCell(1);
                    if (partnerCell != null && partnerCell.getCellType() == CellType.NUMERIC) {
                        didAssignmentDto.setIdPartner((int) partnerCell.getNumericCellValue());
                    } else {
                        // Handle missing or invalid partner_ic
                        didAssignmentDto.setIdPartner(null); // or throw an exception
                    }

                    // Column 2: start_date (as Date)
                    Cell startDateCell = row.getCell(2);
                    if (startDateCell != null && startDateCell.getCellType() == CellType.NUMERIC) {
                        didAssignmentDto.setStartDate(startDateCell.getDateCellValue());
                    } else {
                        // Handle missing or invalid start_date
                        didAssignmentDto.setStartDate(null); // or throw an exception
                    }

                    // Column 3: expiry_da (as Date)
                    Cell expiryDateCell = row.getCell(3);
                    if (expiryDateCell != null && expiryDateCell.getCellType() == CellType.NUMERIC) {
                        didAssignmentDto.setExpiryDate(expiryDateCell.getDateCellValue());
                    } else {
                        // Handle missing or invalid expiry_date
                        didAssignmentDto.setExpiryDate(null); // or throw an exception
                    }

                    // Column 4: descriptio (as String)
                    Cell descriptionCell = row.getCell(4);
                    if (descriptionCell != null) {
                        if (descriptionCell.getCellType() == CellType.STRING) {
                            didAssignmentDto.setDescription(descriptionCell.getStringCellValue().trim());
                        } else if (descriptionCell.getCellType() == CellType.NUMERIC) {
                            didAssignmentDto.setDescription(String.valueOf(descriptionCell.getNumericCellValue()).trim());
                        }
                    }

//                    // Column 5: pool_id (as Integer)
//                    Cell poolIdCell = row.getCell(5);
//                    if (poolIdCell != null && poolIdCell.getCellType() == CellType.NUMERIC) {
//                        didAssignmentDto.setDidPoolId((int) poolIdCell.getNumericCellValue());
//                    } else {
//                        // Handle missing or invalid pool_id
//                        didAssignmentDto.setDidPoolId(null); // or throw an exception
//                    }

                    // Add the DTO to the list
                    didAssignments.add(didAssignmentDto);
                }
            }

            workbook.close();
        }

        return didAssignments;
    }




    // Method to parse date from the CSV format (adjust date format if needed)
    private Date parseDate(String date) throws ParseException, java.text.ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.parse(date);
    }

//    public ResponseEntity<List<DidAssignmentApiResponse>> getDidAssignmentsByDidPoolId(Integer id) {
//        try{
//            List<DidAssignmentApiResponse> responses = new ArrayList<>();
//            for(DidAssignment didAssignment: didAssignmentRepository.findByDidPoolId(id)){
//                Partner partner = partnerRepository.findById(didAssignment.getIdPartner()).orElseThrow(()->new Exception("Could not find partner"));
//                DidAssignmentApiResponse response = new DidAssignmentApiResponse(
//                        didAssignment.getId(),
//                        partner.getIdPartner(),
//                        partner.getPartnerName(),
//                        didAssignment.getDidNumberId(),
//                        didAssignment.getStartDate(),
//                        didAssignment.getExpiryDate(),
//                        didAssignment.getDescription());
//                responses.add(response);
//            }
//            return new ResponseEntity<>(responses, HttpStatus.OK);
////            return new ResponseEntity<>(didPool.getDidAssignments(), HttpStatus.OK);
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    public List<DidAssignment> getDidAssignmentEntities() {
        return didAssignmentRepository.findAll();
    }

//    public List<DidAssignment> getDidAssignmentEntities() {
//        return didAssignmentRepository.findAllDidAssignments();
//    }


}
