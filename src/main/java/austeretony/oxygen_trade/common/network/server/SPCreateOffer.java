package austeretony.oxygen_trade.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_trade.common.main.TradeMain;
import austeretony.oxygen_trade.server.TradeManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPCreateOffer extends Packet {

    private ItemStackWrapper stackWrapper;

    private int amount;

    private long price;

    public SPCreateOffer() {}

    public SPCreateOffer(ItemStackWrapper stackWrapper, int amount, long price) {
        this.stackWrapper = stackWrapper;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        this.stackWrapper.write(buffer);
        buffer.writeShort(this.amount);
        buffer.writeLong(this.price);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {  
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), TradeMain.CREATE_OFFER_REQUEST_ID)) {
            final ItemStackWrapper stackWrapper = ItemStackWrapper.read(buffer);
            final int amount = buffer.readShort();
            final long price = buffer.readLong();
            OxygenHelperServer.addRoutineTask(()->TradeManagerServer.instance().getOffersManager().createOffer(playerMP, stackWrapper, amount, price));
        }
    }
}
