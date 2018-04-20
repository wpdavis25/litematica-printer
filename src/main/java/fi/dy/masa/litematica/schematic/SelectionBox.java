package fi.dy.masa.litematica.schematic;

import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.litematica.util.JsonUtils;
import fi.dy.masa.litematica.util.PositionUtils;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class SelectionBox
{
    private BlockPos pos1;
    private BlockPos pos2;
    private BlockPos size = BlockPos.ORIGIN;
    private String name = "Unnamed";

    @Nullable
    public BlockPos getPos1()
    {
        return this.pos1;
    }

    @Nullable
    public BlockPos getPos2()
    {
        return this.pos2;
    }

    public BlockPos getSize()
    {
        return this.size;
    }

    public String getName()
    {
        return this.name;
    }

    public void setPos1(@Nullable BlockPos pos)
    {
        this.pos1 = pos;
        this.updateSize();
    }

    public void setPos2(@Nullable BlockPos pos)
    {
        this.pos2 = pos;
        this.updateSize();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void rotate(Rotation rotation)
    {
        BlockPos pos = PositionUtils.getTransformedBlockPos(this.getSize(), Mirror.NONE, rotation);
        this.setPos2(this.getPos1().add(pos).add(-1, -1, -1));
    }

    public void mirror(Mirror mirror)
    {
        BlockPos pos = PositionUtils.getTransformedBlockPos(this.getSize(), mirror, Rotation.NONE);
        this.setPos2(this.getPos1().add(pos).add(-1, -1, -1));
    }

    private void updateSize()
    {
        if (this.pos1 != null && this.pos2 != null)
        {
            this.size = this.pos2.subtract(this.pos1).add(1, 1, 1);
        }
        else if (this.pos1 == null && this.pos2 == null)
        {
            this.size = BlockPos.ORIGIN;
        }
        else
        {
            this.size = new BlockPos(1, 1, 1);
        }
    }

    @Nullable
    public static SelectionBox fromJson(JsonObject obj)
    {
        SelectionBox box = new SelectionBox();

        if (JsonUtils.hasArray(obj, "pos1"))
        {
            JsonArray arr = obj.get("pos1").getAsJsonArray();
            box.setPos1(blockPosFromJson(arr));
        }

        if (JsonUtils.hasArray(obj, "pos2"))
        {
            JsonArray arr = obj.get("pos2").getAsJsonArray();
            box.setPos2(blockPosFromJson(arr));
        }

        if (JsonUtils.hasString(obj, "name"))
        {
            box.setName(obj.get("name").getAsString());
        }

        return (box.getPos1() != null || box.getPos2() != null) ? box : null;
    }

    @Nullable
    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        if (this.pos1 != null)
        {
            obj.add("pos1", blockPosToJson(this.pos1));
        }

        if (this.pos2 != null)
        {
            obj.add("pos2", blockPosToJson(this.pos2));
        }

        obj.add("name", new JsonPrimitive(this.name));

        return this.pos1 != null || this.pos2 != null ? obj : null;
    }

    public static JsonArray blockPosToJson(BlockPos pos)
    {
        JsonArray arr = new JsonArray();

        arr.add(pos.getX());
        arr.add(pos.getY());
        arr.add(pos.getZ());

        return arr;
    }

    @Nullable
    public static BlockPos blockPosFromJson(JsonArray arr)
    {
        if (arr.size() == 3)
        {
            try
            {
                return new BlockPos(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
            }
            catch (Exception e)
            {
            }
        }

        return null;
    }
}