package quote.fsrod.common.item.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.utils.ModLogger;

public interface IItemHasFileData {
    public static final String TAG_FILE_NAME = "file_name";

    public String getBasePath();

    public static Optional<String> getBasePathOfStack(ItemStack stack){
        if(stack.getItem() instanceof IItemHasFileData){
            String filePath = ((IItemHasFileData)stack.getItem()).getBasePath();
            if(!filePath.isEmpty()){
                return Optional.of(filePath);
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getFileName(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();

        if(tag.contains(TAG_FILE_NAME)){
            return Optional.of(tag.getString(TAG_FILE_NAME));
        }
        return Optional.empty();
    }

    @SuppressWarnings("resource")
    private static File getFile(ItemStack stack, String basePath){
        // get saves directory : "%GAME_DIR%/saves"
        File savesDir = Minecraft.getInstance().gameDirectory;
        String filePath = IItemHasFileData.getFileName(stack).orElse("");
        File file = new File(savesDir, basePath + filePath);

        return file;
    }

    public static boolean existsFile(ItemStack stack){
        Optional<String> possibleBasePath = getBasePathOfStack(stack);
        if(possibleBasePath.isEmpty()) return false;
        File file = getFile(stack, possibleBasePath.get());

        try {
            if(!Files.exists(file.toPath().getParent())){
                Files.createDirectories(file.toPath().getParent());
                return false;
            }
            if(!Files.exists(file.toPath())){
                return false;
            }

        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
            return false;
        }

        return true;
    }

    public static boolean saveTag(CompoundTag tag, ItemStack stack){
        Optional<String> possibleBasePath = getBasePathOfStack(stack);
        if(possibleBasePath.isEmpty()) return false;
        File file = getFile(stack, possibleBasePath.get());

        try {
            if(!file.createNewFile()){
                return false;
            }
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }

        try (FileOutputStream fileoutputstream = new FileOutputStream(file)) {
            NbtIo.writeCompressed(tag, fileoutputstream);
            return true;
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }

        return false;
    }

    public static CompoundTag loadTag(ItemStack stack){
        Optional<String> possibleBasePath = getBasePathOfStack(stack);
        if(possibleBasePath.isEmpty()) return new CompoundTag();
        File file = getFile(stack, possibleBasePath.get());

        if(!file.exists()) return new CompoundTag();

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return NbtIo.readCompressed(fileInputStream);
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }

        return new CompoundTag();
    }
}
