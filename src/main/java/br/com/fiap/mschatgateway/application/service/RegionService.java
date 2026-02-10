package br.com.fiap.mschatgateway.application.service;

import br.com.fiap.mschatgateway.domain.model.PharmacyRegion;
import br.com.fiap.mschatgateway.infrastructure.data.PharmacyRegionLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    private final PharmacyRegionLoader loader;

    public RegionService(PharmacyRegionLoader loader) {
        this.loader = loader;
    }

    public List<String> getStates() {
        return loader.getAll().stream()
                .map(PharmacyRegion::getState)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getCities(String state) {
        return loader.getAll().stream()
                .filter(r -> r.getState().equalsIgnoreCase(state))
                .map(PharmacyRegion::getCity)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getNeighborhoods(String state, String city) {
        return loader.getAll().stream()
                .filter(r -> r.getState().equalsIgnoreCase(state))
                .filter(r -> r.getCity().equalsIgnoreCase(city))
                .map(PharmacyRegion::getNeighborhood)
                .distinct()
                .sorted()
                .toList();
    }

    public List<PharmacyRegion> getPharmacies(
            String state,
            String city,
            String neighborhood
    ) {
        return loader.getAll().stream()
                .filter(r -> r.getState().equalsIgnoreCase(state))
                .filter(r -> r.getCity().equalsIgnoreCase(city))
                .filter(r -> r.getNeighborhood().equalsIgnoreCase(neighborhood))
                .toList();
    }
}
