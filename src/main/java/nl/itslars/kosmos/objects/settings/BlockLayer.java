package nl.itslars.kosmos.objects.settings;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BlockLayer {

    @SerializedName("block_name")
    private String blockName;
    @SerializedName("block_data")
    private int blockData = 0;
    @SerializedName("count")
    private int count = 1;
}
