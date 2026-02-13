package br.com.fiap.mschatgateway.adapters.outbound.region;

import br.com.fiap.mschatgateway.domain.model.region.PharmacyRegion;
import br.com.fiap.mschatgateway.domain.ports.outbound.RegionCatalogPort;
import br.com.fiap.mschatgateway.infrastructure.data.PharmacyRegionLoader;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class JsonRegionCatalogAdapter implements RegionCatalogPort {

    private final PharmacyRegionLoader loader;

    public JsonRegionCatalogAdapter(PharmacyRegionLoader loader) {
        this.loader = loader;
    }

    private List<PharmacyRegion> regions() {
        return loader.getAll() != null ? loader.getAll() : Collections.emptyList();
    }

    @Override
    public List<String> getStates() {
        return regions().stream()
                .map(PharmacyRegion::getState)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> getCities(String state) {
        return regions().stream()
                .filter(r -> equalsIgnoreCase(r.getState(), state))
                .map(PharmacyRegion::getCity)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> getNeighborhoods(String state, String city) {
        return regions().stream()
                .filter(r -> equalsIgnoreCase(r.getState(), state))
                .filter(r -> equalsIgnoreCase(r.getCity(), city))
                .map(PharmacyRegion::getNeighborhood)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && a.equalsIgnoreCase(b);
    }
}
