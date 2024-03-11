package unsafedodo.fabricauctionhouse.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.auction.AuctionItem;
import unsafedodo.fabricauctionhouse.util.EconomyHandler;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GUIAuctionItem extends SimpleGui {
    private int ticker = 0;
    private final AuctionItem item;
    public GUIAuctionItem(ServerPlayerEntity player, AuctionItem item) {
        super(ScreenHandlerType.GENERIC_9X1, player, false);
        this.item = item;
    }

    void updateDisplay() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 9; i++) {
            var navElement = this.getNavElement(i);

            if(navElement == null)
                navElement = DisplayElement.EMPTY;

            if(navElement.element != null){
                this.setSlot(i, navElement.element);
            } else if(navElement.slot != null){
                this.setSlotRedirect(i, navElement.slot);
            }
        }
    }

    protected DisplayElement getNavElement(int id) throws ExecutionException, InterruptedException {
        return switch (id){
            case 0 -> DisplayElement.of(GuiElementBuilder.from(Items.CLOCK.getDefaultStack())
                            .setName(Text.literal("Time left: "+item.getTimeLeft()).formatted(Formatting.BLUE))
                            .hideFlags());
            case 1 -> DisplayElement.of(GuiElementBuilder.from(Items.PAPER.getDefaultStack())
                            .setName(Text.literal(String.format("Price: %.2f $", item.getPrice())).formatted(Formatting.BLUE))
                            .hideFlags());

            case 2 -> skull();

            case 4 -> DisplayElement.of(GuiElementBuilder.from(item.getItemStack()));

            case 6 -> confirm();

            case 7 -> DisplayElement.of(GuiElementBuilder.from(Items.RED_STAINED_GLASS_PANE.getDefaultStack())
                            .setName(Text.literal("Cancel").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback(((index, type1, action) -> {
                                playClickSound(this.player);
                                this.close();
                            })));

            case 8 -> trash();

            default -> DisplayElement.of(GuiElementBuilder.from(Items.LIGHT_GRAY_STAINED_GLASS_PANE.getDefaultStack())
                            .setName(Text.empty())
                            .hideFlags());
        };
    }

    @Override
    public void onTick(){
        ticker++;
        if(ticker >= 5){
            ticker = 0;
            try {
                updateDisplay();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        super.onTick();
    }

    public static void playClickSound(ServerPlayerEntity player){
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);
    }

    private DisplayElement confirm() throws ExecutionException, InterruptedException {
        if(item.getPrice() < EconomyHandler.getBalance(EconomyHandler.getAccount(player.getUuid()))){
            return DisplayElement.of(GuiElementBuilder.from(Items.GREEN_STAINED_GLASS_PANE.getDefaultStack())
                            .setName(Text.literal("Confirm").formatted(Formatting.GREEN))
                            .hideFlags()
                            .setCallback(((index, type1, action) -> {
                                playClickSound(this.player);
                                this.buy();
                            })));
        } else {
            return DisplayElement.of(GuiElementBuilder.from(Items.GRAY_STAINED_GLASS_PANE.getDefaultStack())
                            .setName(Text.literal("Confirm").formatted(Formatting.DARK_GRAY))
                            .hideFlags());
        }
    }

    private DisplayElement trash(){
        if(player.hasPermissionLevel(4) || player.getUuidAsString().equals(item.getUuid())){
            return DisplayElement.of(GuiElementBuilder.from(Items.HOPPER.getDefaultStack())
                            .setName(Text.literal("Remove from auction").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback(((index, type1, action) -> {
                                playClickSound(this.player);
                                this.remove();
                            })));
        } else {
            return DisplayElement.of(GuiElementBuilder.from(ItemStack.EMPTY));
        }
    }

    private DisplayElement skull(){
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateNbt().putString("SkullOwner", item.getOwner());
        return DisplayElement.of(GuiElementBuilder.from(stack)
                        .setName(Text.literal("Owner: "+ item.getOwner()).formatted(Formatting.BLUE))
                        .hideFlags());
    }

    @Override
    public void close(){
        GUIAuctionHouse gui = new GUIAuctionHouse(player);
        try {
            gui.updateDisplay();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        gui.setTitle(Text.literal("Auction House"));
        gui.open();
        super.close();
    }

    private void remove(){
        AuctionHouseMain.getDatabaseManager().expireItem(item);
        close();
    }

    private void buy(){
        if(AuctionHouseMain.getDatabaseManager().isItemForAuction(item.getId())){
            if(player.getInventory().getEmptySlot() != -1){
                if(EconomyHandler.transfer(EconomyHandler.getAccount(player.getUuid()), EconomyHandler.getAccount(UUID.fromString(item.getUuid())), item.getPrice())){
                    AuctionHouseMain.getDatabaseManager().removeItemFromAuction(item);
                    player.sendMessage(Text.literal("You have purchased ").formatted(Formatting.GREEN)
                            .append(Text.literal(String.valueOf(item.getItemStack().getCount())).formatted(Formatting.GREEN))
                            .append(Text.literal(" ").formatted(Formatting.GREEN))
                            .append(Text.literal(item.getDisplayName()).formatted(Formatting.DARK_PURPLE))
                            .append(Text.literal(" from ").formatted(Formatting.GREEN))
                            .append(Text.literal(item.getOwner()).formatted(Formatting.YELLOW))
                            .append(Text.literal(" for "+item.getPrice()+" $").formatted(Formatting.GREEN)));
                    player.getInventory().offerOrDrop(item.getItemStack());

                }
            } else
                player.sendMessage(Text.literal("You don't have any empty slot in your inventory").formatted(Formatting.RED));
        } else
            player.sendMessage(Text.literal("That item was already bought").formatted(Formatting.RED));

        super.close();
    }

    public record DisplayElement(@Nullable GuiElementInterface element, @Nullable Slot slot){
        private static final DisplayElement EMPTY = DisplayElement.of(new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK));
        private static final DisplayElement FILLER = DisplayElement.of(new GuiElementBuilder(Items.LIGHT_GRAY_STAINED_GLASS_PANE).setName(Text.literal("")).hideFlags());

        public static DisplayElement of(GuiElementInterface element){
            return new DisplayElement(element, null);
        }

        public static DisplayElement of(GuiElementBuilderInterface<?> element){
            return new DisplayElement(element.build(), null);
        }

        public static DisplayElement filler(){
            return FILLER;
        }
    }
}
