package com.ewe.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.FleetDetailsForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.FleetDetails;
import com.ewe.pojo.Fleet_Vehicle;
import com.ewe.service.FleetService;

@RestController
@RequestMapping("/services/fleet")
public class FleetController {
    @Autowired
    private FleetService fleetService;
    
  @ApiOperation(value = "Add a new Fleet")
  @RequestMapping(value = "/addFleet", method = RequestMethod.POST)
  public ResponseEntity<ResponseMessage> addFleet(@RequestBody FleetDetailsForm fleet)
      throws UserNotFoundException, DuplicateUserException {
      fleetService.addFleet(fleet);
      String msg = "Fleet added successfully";
      return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
  }

    @ApiOperation(value = "Get Fleet Details By Id")
    @RequestMapping(value = "/fleetDetails/{id}", method = RequestMethod.GET)
    public ResponseEntity<FleetDetails> getFleetById(@PathVariable Long id)
        throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(fleetService.getFleetById(id));
    }
    
    @ApiOperation(value = "Get all fleet details (pagination + search)")
    @GetMapping("/getAllFleets")
    public ResponseEntity<?> getAllFleets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) throws UserNotFoundException {

        return ResponseEntity.ok(fleetService.getAllFleets(page, size, search));
    }
    
    @ApiOperation(value = "Edit Fleet")
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT) 
    public ResponseEntity<ResponseMessage> editFleet(
        @PathVariable Long id, @RequestBody(required = false) FleetDetailsForm fleetForm)
        throws UserNotFoundException, DuplicateUserException {
        fleetService.editFleet(id, fleetForm);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Fleet Successfully Updated"));
    }
    
    @ApiOperation(value = "Delete Fleet")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseMessage> deleteFleet(@PathVariable Long id)
        throws UserNotFoundException {
        fleetService.deleteFleet(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Fleet deleted successfully"));
    }
    
    @ApiOperation(value = "Add a Vehicle to Fleet")
    @RequestMapping(value = "/{fleetId}/addVehicle", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> addVehicleToFleet(
        @PathVariable Long fleetId, @RequestBody FleetDetailsForm vehicleForm)
        throws UserNotFoundException, DuplicateUserException {
        fleetService.addVehicleToFleet(fleetId, vehicleForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Vehicle added to fleet successfully"));    
    }

    @ApiOperation(value = "Get all vehicles in a fleet")
    @RequestMapping(value = "/{fleetId}/vehicles", method = RequestMethod.GET)
    public ResponseEntity<List<Fleet_Vehicle>> getFleetVehicles(@PathVariable Long fleetId)
        throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(fleetService.getFleetVehicles(fleetId));
    }

    @ApiOperation(value = "Get vehicle details by number")
    @RequestMapping(value = "/vehicle/{vehicleNumber}", method = RequestMethod.GET)
    public ResponseEntity<Fleet_Vehicle> getVehicleByNumber(@PathVariable String vehicleNumber)
        throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(fleetService.getVehicleByNumber(vehicleNumber));
    }
    @ApiOperation(value = "Update vehicle details")
    @RequestMapping(value = "/update/{vehicleNumber}", method = RequestMethod.PUT) 
    public ResponseEntity<ResponseMessage> updateVehicle(
        @PathVariable String vehicleNumber, @RequestBody FleetDetailsForm vehicleForm)
        throws UserNotFoundException {
        fleetService.updateVehicle(vehicleNumber, vehicleForm);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Vehicle updated successfully"));
    }

    @ApiOperation(value = "Delete vehicle from fleet")
    @RequestMapping(value = "/{fleetId}/vehicle/{vehicleNumber}", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseMessage> deleteVehicleFromFleet(
        @PathVariable Long fleetId, @PathVariable String vehicleNumber)
        throws UserNotFoundException {
        fleetService.deleteVehicleFromFleet(fleetId, vehicleNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Vehicle deleted from fleet successfully"));
    }

}