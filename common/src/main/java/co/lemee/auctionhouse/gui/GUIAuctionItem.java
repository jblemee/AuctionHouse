package co.lemee.auctionhouse.gui;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionItem;
import co.lemee.auctionhouse.economy.EconomyHandler;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GUIAuctionItem extends SimpleGui {
    private final AuctionItem item;
    private int ticker = 0;

    public GUIAuctionItem(ServerPlayer player, AuctionItem item) {
        super(MenuType.GENERIC_9x6, player, false);
        this.item = item;
    }

    public static void playClickSound(ServerPlayer player) {
        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 1, 1);
    }

    void updateDisplay() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 9; i++) {
            var navElement = this.getNavElement(i);

            if (navElement == null)
                navElement = DisplayElement.EMPTY;

            if (navElement.element != null) {
                this.setSlot(i, navElement.element);
            } else if (navElement.slot != null) {
                this.setSlotRedirect(i, navElement.slot);
            }
        }
    }

    protected DisplayElement getNavElement(int id) throws ExecutionException, InterruptedException {
        return switch (id) {
            case 0 -> DisplayElement.of(GuiElementBuilder.from(Items.CLOCK.getDefaultInstance())
                    .setName(Component.literal("Time left: " + item.getTimeLeft()).withStyle(ChatFormatting.BLUE))
                    .hideDefaultTooltip());
            case 1 -> DisplayElement.of(GuiElementBuilder.from(Items.PAPER.getDefaultInstance())
                    .setName(Component.literal(String.format("Price: %.2f $", item.getPrice())).withStyle(ChatFormatting.BLUE))
                    .hideDefaultTooltip());

            case 2 -> skull();

            case 4 -> DisplayElement.of(GuiElementBuilder.from(item.getItemStack()));

            case 6 -> confirm();

            case 7 -> DisplayElement.of(GuiElementBuilder.from(Items.RED_STAINED_GLASS_PANE.getDefaultInstance())
                    .setName(Component.literal("Cancel").withStyle(ChatFormatting.RED))
                    .hideDefaultTooltip()
                    .setCallback(((index, type1, action) -> {
                        playClickSound(this.player);
                        this.close();
                    })));

            case 8 -> trash();

            default -> DisplayElement.of(GuiElementBuilder.from(Items.LIGHT_GRAY_STAINED_GLASS_PANE.getDefaultInstance())
                    .setName(Component.empty())
                    .hideDefaultTooltip());
        };
    }

    @Override
    public void onTick() {
        ticker++;
        if (ticker >= 5) {
            ticker = 0;
            try {
                updateDisplay();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        super.onTick();
    }

    private DisplayElement confirm() throws ExecutionException, InterruptedException {
        if (item.getPrice() < EconomyHandler.getInstance().getBalance(player.getUUID())) {
            return DisplayElement.of(GuiElementBuilder.from(Items.GREEN_STAINED_GLASS_PANE.getDefaultInstance())
                    .setName(Component.literal("Confirm").withStyle(ChatFormatting.GREEN))
                    .hideDefaultTooltip()
                    .setCallback(((index, type1, action) -> {
                        playClickSound(this.player);
                        this.buy();
                    })));
        } else {
            return DisplayElement.of(GuiElementBuilder.from(Items.GRAY_STAINED_GLASS_PANE.getDefaultInstance())
                    .setName(Component.literal("Confirm").withStyle(ChatFormatting.DARK_GRAY))
                    .hideDefaultTooltip());
        }
    }

    private DisplayElement trash() {
        if (player.hasPermissions(4) || player.getStringUUID().equals(item.getUuid())) {
            return DisplayElement.of(GuiElementBuilder.from(Items.HOPPER.getDefaultInstance())
                    .setName(Component.literal("Remove from auction").withStyle(ChatFormatting.RED))
                    .hideDefaultTooltip()
                    .setCallback(((index, type1, action) -> {
                        playClickSound(this.player);
                        this.remove();
                    })));
        } else {
            return DisplayElement.of(GuiElementBuilder.from(ItemStack.EMPTY));
        }
    }

    private DisplayElement skull() {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        return DisplayElement.of(GuiElementBuilder.from(stack)
                .setName(Component.literal("Owner: " + item.getOwner()).withStyle(ChatFormatting.BLUE))
                .hideDefaultTooltip());
    }

    @Override
    public void close() {
        GUIAuctionHouse gui = new GUIAuctionHouse(player);
        try {
            gui.updateDisplay();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        gui.setTitle(Component.literal("Auction House"));
        gui.open();
        super.close();
    }

    private void remove() {
        AuctionHouseMod.getDatabaseManager().expireItem(item);
        close();
    }

    private void buy() {
        if (AuctionHouseMod.getDatabaseManager().isItemForAuction(item.getId())) {
            if (player.getInventory().getFreeSlot() != -1) {
                if (EconomyHandler.getInstance().transfer(player.getUUID(), UUID.fromString(item.getUuid()), item.getPrice())) {
                    AuctionHouseMod.getDatabaseManager().removeItemFromAuction(item);
                    player.sendSystemMessage(Component.literal("You have purchased ").withStyle(ChatFormatting.GREEN)
                            .append(Component.literal(String.valueOf(item.getItemStack().getCount())).withStyle(ChatFormatting.GREEN))
                            .append(Component.literal(" ").withStyle(ChatFormatting.GREEN))
                            .append(Component.literal(item.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE))
                            .append(Component.literal(" from ").withStyle(ChatFormatting.GREEN))
                            .append(Component.literal(item.getOwner()).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal(" for " + item.getPrice() + " $").withStyle(ChatFormatting.GREEN)));
                    player.getInventory().add(item.getItemStack());

                }
            } else
                player.sendSystemMessage(Component.literal("You don't have any empty slot in your inventory").withStyle(ChatFormatting.RED));
        } else
            player.sendSystemMessage(Component.literal("That item was already bought").withStyle(ChatFormatting.RED));

        super.close();
    }

    public record DisplayElement(@Nullable GuiElementInterface element, @Nullable Slot slot) {
        private static final DisplayElement EMPTY = DisplayElement.of(new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK));
        private static final DisplayElement FILLER = DisplayElement.of(new GuiElementBuilder(Items.LIGHT_GRAY_STAINED_GLASS_PANE).setName(Component.literal("")).hideDefaultTooltip());

        public static DisplayElement of(GuiElementInterface element) {
            return new DisplayElement(element, null);
        }

        public static DisplayElement of(GuiElementBuilderInterface<?> element) {
            return new DisplayElement(element.build(), null);
        }

        public static DisplayElement filler() {
            return FILLER;
        }
    }
}
