package nl.itslars.kosmos.objects.settings;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import nl.itslars.kosmos.enums.Biome;

import java.util.List;

@Getter
@Setter
public class FlatWorldLayers {

    // Biome
    @SerializedName("biome_id")
    private Biome biome;

    // List of block layers
    @SerializedName("block_layers")
    private List<BlockLayer> blockLayers;

    // Version of the json. Currently 4 is the latest
    @SerializedName("encoding_version")
    private int version = 4;

    // Specific behavior unknown, always null
    @SerializedName("structure_options")
    private Object structureOptions;

}
