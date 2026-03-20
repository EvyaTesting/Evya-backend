package com.ewe.serviceImpl;

import com.ewe.controller.advice.ServerException;
import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ReferralCodeRequest;
import com.ewe.form.VehicleForm;
import com.ewe.pojo.Referral;
import com.ewe.pojo.Site;
import com.ewe.service.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReferralServiceImp implements ReferralService {

    @Autowired
    private GeneralDao generalDao;

    @Override
    @Transactional
    public Referral createReferralCode(ReferralCodeRequest request) throws UserNotFoundException {
        Referral referral = new Referral();
        referral.setOfferPercentage(request.getOfferPercentage());
        referral.setValidFrom(request.getValidFrom());
        referral.setValidTo(request.getValidTo());

        boolean isAllSites = request.getAllSites() != null && request.getAllSites();
        referral.setAllSites(isAllSites);

        Set<Site> sites = new HashSet<>();

        if (isAllSites) {
            sites.addAll(generalDao.findAll(Site.class));
        } else if (request.getSiteIds() != null && !request.getSiteIds().isEmpty()) {
            sites.addAll(generalDao.findAllByIds(Site.class, request.getSiteIds()));
        }

        referral.setSites(sites);

        // Generate referral code
        String prefix;
        if (isAllSites) {
            prefix = "ST";
        } else if (!sites.isEmpty()) {
            String siteName = sites.iterator().next().getSiteName();
            prefix = siteName.length() >= 2 ? siteName.substring(0, 2).toUpperCase() : siteName.toUpperCase();
        } else {
            prefix = "XX";
        }

        String randomDigits = String.valueOf(1000 + new Random().nextInt(9000));
        referral.setReferralCode(prefix + randomDigits);

        generalDao.save(referral);
        return referral;
    }

    @Override
    @Transactional
    public void updateReferralCode(Long id, ReferralCodeRequest request) throws UserNotFoundException {
        Referral existingReferral = findByReferralId(id);
        if (existingReferral == null) {
            throw new UserNotFoundException("Referral not found with ID: " + id);
        }

        validateReferral(request, existingReferral);

        List<Long> updateSiteIds = request.getSiteIds() != null ? request.getSiteIds() : new ArrayList<>();

        // Create new referral for updated sites
        Referral newReferral = new Referral();
        newReferral.setOfferPercentage(request.getOfferPercentage());
        newReferral.setValidFrom(request.getValidFrom());
        newReferral.setValidTo(request.getValidTo());
        newReferral.setAllSites(false);

        List<Site> updatedSites = generalDao.findAllByIds(Site.class, updateSiteIds);
        newReferral.setSites(new HashSet<>(updatedSites));

        if (!updatedSites.isEmpty()) {
            String siteName = updatedSites.get(0).getSiteName();
            String prefix = siteName.length() >= 2 ? siteName.substring(0, 2).toUpperCase() : siteName.toUpperCase();
            String randomDigits = String.valueOf(1000 + new Random().nextInt(9000));
            newReferral.setReferralCode(prefix + randomDigits);
        }

        generalDao.save(newReferral);

        // Remove updated sites from old referral
        Set<Site> remainingSites = existingReferral.getSites().stream()
                .filter(site -> !updateSiteIds.contains(site.getId()))
                .collect(Collectors.toSet());

        existingReferral.setSites(remainingSites);
        generalDao.savOrupdate(existingReferral);
    }

    private void validateReferral(ReferralCodeRequest request, Referral referral) {
        if (request.getValidFrom() != null && request.getValidTo() != null) {
            if (request.getValidFrom().after(request.getValidTo())) {
                throw new IllegalArgumentException("validFrom must be before validTo");
            }
        }

        if (Boolean.TRUE.equals(request.getAllSites()) && request.getSiteIds() != null && !request.getSiteIds().isEmpty()) {
            throw new IllegalArgumentException("siteIds must be empty when allSites is true");
        }

        if (request.getOfferPercentage() != null &&
                (request.getOfferPercentage() < 0 || request.getOfferPercentage() > 100)) {
            throw new IllegalArgumentException("offerPercentage must be between 0 and 100");
        }
    }

    @Override
    @Transactional
    public Referral findByReferralId(Long id) {
        try {
            String query = "SELECT * FROM referral_code WHERE id = " + id;
            return (Referral) generalDao.findOneSQLQuery(new Referral(), query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteReferralCode(Long id) throws UserNotFoundException {
        Referral existingReferral = findByReferralId(id);

        if (existingReferral == null) {
            throw new UserNotFoundException("Referral not found with ID: " + id);
        }
        existingReferral.setSites(new HashSet<>());
        generalDao.delete(existingReferral);
    }

    @Override
    public List<Referral> getAllReferrals() {
        return (List<Referral>) generalDao.findAll(new Referral())
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

	
}
