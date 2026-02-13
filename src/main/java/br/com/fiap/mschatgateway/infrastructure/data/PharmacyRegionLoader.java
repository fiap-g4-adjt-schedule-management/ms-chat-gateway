package br.com.fiap.mschatgateway.infrastructure.data;

import br.com.fiap.mschatgateway.domain.model.region.PharmacyRegion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Component
public class PharmacyRegionLoader {

    private List<PharmacyRegion> regions;

    @PostConstruct
    public void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("data/farmacia-popular-regions.json");

            this.regions = mapper.readValue(
                    is,
                    new TypeReference<List<PharmacyRegion>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar as regiões do Farmácia Popular", e);
        }
    }

    public List<PharmacyRegion> getAll() {
        return regions;
    }
}

