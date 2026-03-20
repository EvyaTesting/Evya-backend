package com.ewe.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ChargerForm;
import com.ewe.form.ManufacturerForm;
import com.ewe.form.PortForm;
import com.ewe.pojo.ChargerDetails;
import com.ewe.pojo.ConnectorDetails;
import com.ewe.pojo.ManufacturerDetails;
import com.ewe.service.ManufacturerService;

@Service
public class ManufacturerServiceImpl  implements ManufacturerService {

	@Autowired
	private GeneralDao<?, ?> generalDao;
	
	@Override
	public void add(ManufacturerForm manufacturerForm) throws UserNotFoundException {
		// Save manufacturer
		ManufacturerDetails manufacturer = new ManufacturerDetails();
		manufacturer.setManufacturerName(manufacturerForm.getManufacturerName());
		manufacturer.setCountry(manufacturerForm.getCountry());
		manufacturer.setContactInfo(manufacturerForm.getContactInfo());
		manufacturer.setMobileNumber(manufacturerForm.getMobileNumber());
		manufacturer = generalDao.save(manufacturer);

		// Save chargers and their ports
		if (manufacturerForm.getChargers() != null) {
			for (ChargerForm chargerForm : manufacturerForm.getChargers()) {
				ChargerDetails station = new ChargerDetails();
				station.setChargerType(chargerForm.getChargerType());
				station.setTotalCapacityKW(chargerForm.getTotalCapacityKW());
				station.setCurrentType(chargerForm.getCurrentType());
				
				//station.setPortQuantity(chargerForm.getPortQuantity());
				station.setManufacturerDetails(manufacturer);
				station = generalDao.save(station);
				int portCount = 0;
				// Save ports
				if (chargerForm.getPorts() != null) {
					for (PortForm portForm : chargerForm.getPorts()) {
						ConnectorDetails port = new ConnectorDetails();
						port.setConnectorType(portForm.getConnectorType());
						port.setPortCapacityKW(portForm.getPortCapacityKW());
						port.setMaxInputVoltageV(portForm.getMaxInputVoltageV());
						port.setMaxOutputVoltageV(portForm.getMaxOutputVoltageV());
						port.setOutputCurrentA(portForm.getOutputCurrentA());
						port.setPortDisplayName(portForm.getPortDisplayName());
						port.setChargingStation(station);
						// port.setManufacturerDetails(manufacturer.getId());
						generalDao.save(port);
						portCount++;
					}
				}
				station.setPortQuantity(portCount);
			    generalDao.save(station);
			}
		}

	}

	@Override
	public List<ManufacturerDetails> getALLManufacturers() {
		try {
			return generalDao.findAllSQLQuery(new ManufacturerDetails(), "Select * from manufacturer_details");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ManufacturerDetails getManufracturerById(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from manufacturer_details where id = " + id;
			ManufacturerDetails net = generalDao.findOneSQLQuery(new ManufacturerDetails(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ManufacturerDetails> getAllManufacturerPaginated() throws UserNotFoundException {
	    return getAllManufacturerPaginated(null); // Maintain backward compatibility
	}

	@Override
	public List<ManufacturerDetails> getAllManufacturerPaginated(String search) throws UserNotFoundException {
	    if (search == null || search.trim().isEmpty()) {
	        return generalDao.findAll(new ManufacturerDetails());
	    }
	    
	    String searchTerm = "%" + search.toLowerCase() + "%";
	    String hql = "FROM ManufacturerDetails WHERE " +
	                "LOWER(manufacturerName) LIKE '" + searchTerm + "' OR " +
	                "LOWER(country) LIKE '" + searchTerm + "' OR " +
	                "LOWER(contactInfo) LIKE '" + searchTerm + "' OR " +
	                "mobileNumber LIKE '" + searchTerm + "'";
	    
	    return generalDao.findAllHQLQuery(new ManufacturerDetails(), hql);
	}
	public void deleteManufacturerById(Long id) throws UserNotFoundException {
	    try {
	    	 ManufacturerDetails manufactuerTemplate = new ManufacturerDetails();
	    	
	        ManufacturerDetails manufacturer = generalDao.findOneById(manufactuerTemplate, id);
	        if (manufacturer == null) throw new UserNotFoundException("Manufacturer not found with id: " + id);
	        generalDao.delete(manufacturer);
	    } catch (Exception e) {
	        throw new UserNotFoundException("Failed to delete manufacturer: " + e.getMessage(), e);
	    }
	}

	@Override
	@Transactional
	public void addCharger(Long id, ManufacturerForm form) throws UserNotFoundException {
	    ManufacturerDetails manufacturer = getManufracturerById(id);
	    if (manufacturer == null) {
	        throw new RuntimeException("Manufacturer not found with ID: " + id);
	    }

	    // Update manufacturer basic info
	    manufacturer.setManufacturerName(form.getManufacturerName());
	    manufacturer.setCountry(form.getCountry());
	    manufacturer.setContactInfo(form.getContactInfo());
	    manufacturer.setMobileNumber(form.getMobileNumber());

	    // Maintain current chargers to update only what's needed
	    Set<ChargerDetails> existingChargers = manufacturer.getChargingStation();
	    Set<ChargerDetails> updatedChargers = new HashSet<>();

	    if (form.getChargers() != null) {
	        for (ChargerForm chargerForm : form.getChargers()) {
	            ChargerDetails charger;

	            if (chargerForm.getId() != null) {
	                charger = findById(chargerForm.getId());
	                if (charger == null) {
	                    throw new RuntimeException("Charger not found with ID: " + chargerForm.getId());
	                }
	            } else {
	                charger = new ChargerDetails();
	            }

	            charger.setChargerType(chargerForm.getChargerType());
	            charger.setCurrentType(chargerForm.getCurrentType());
	            charger.setPortQuantity(chargerForm.getPortQuantity());
	            charger.setTotalCapacityKW(chargerForm.getTotalCapacityKW());

	            charger.setManufacturerDetails(manufacturer); // Always set owner

	            // Update connectors
	            List<ConnectorDetails> updatedConnectors = new ArrayList<>();
	            if (chargerForm.getPorts() != null) {
	                for (PortForm portForm : chargerForm.getPorts()) {
	                    ConnectorDetails connector;

	                    if (portForm.getId() != null) {
	                        connector = findBId(portForm.getId());
	                        if (connector == null) {
	                            throw new RuntimeException("Port not found with ID: " + portForm.getId());
	                        }
	                    } else {
	                        connector = new ConnectorDetails();
	                    }

	                    connector.setConnectorType(portForm.getConnectorType());
	                    connector.setPortCapacityKW(portForm.getPortCapacityKW());
	                    connector.setMaxInputVoltageV(portForm.getMaxInputVoltageV());
	                    connector.setMaxOutputVoltageV(portForm.getMaxOutputVoltageV());
	                    connector.setOutputCurrentA(portForm.getOutputCurrentA());
	                    connector.setPortDisplayName(portForm.getPortDisplayName());
	                    connector.setChargingStation(charger); // Always set back reference

	                    updatedConnectors.add(connector);
	                }
	            }

	            charger.setChargingPort(updatedConnectors);
	            updatedChargers.add(charger);
	        }
	    }

	    // Instead of replacing, clear and add
	    manufacturer.getChargingStation().clear();
	    manufacturer.getChargingStation().addAll(updatedChargers);

	    generalDao.savOrupdate(manufacturer);
	}


	@Override
	public ChargerDetails findById(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from charger_details where id = " + id;
			ChargerDetails net = generalDao.findOneSQLQuery(new ChargerDetails(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConnectorDetails findBId(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from connector_details where id = " + id;
			ConnectorDetails net = generalDao.findOneSQLQuery(new ConnectorDetails(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	}