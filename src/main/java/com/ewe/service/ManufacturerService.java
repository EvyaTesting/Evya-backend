package com.ewe.service;

import java.util.List;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ManufacturerForm;
import com.ewe.pojo.ChargerDetails;
import com.ewe.pojo.ConnectorDetails;
import com.ewe.pojo.ManufacturerDetails;

public interface ManufacturerService {

	void add(ManufacturerForm mannform) throws UserNotFoundException;

	List<ManufacturerDetails> getALLManufacturers();

	ManufacturerDetails getManufracturerById(Long id);

	List<ManufacturerDetails> getAllManufacturerPaginated(String search) throws UserNotFoundException;
	List<ManufacturerDetails> getAllManufacturerPaginated() throws UserNotFoundException;

	void deleteManufacturerById(Long id) throws UserNotFoundException;

	void addCharger( Long id,ManufacturerForm manform) throws UserNotFoundException;
	ChargerDetails findById(Long id);
	ConnectorDetails findBId(Long id);
}