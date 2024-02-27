package com.rs.plugin.impl.objects.region;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.constants.Animations;
import com.rs.constants.Graphic;
import com.rs.constants.Sounds;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.map.GameObject;
import com.rs.game.map.World;
import com.rs.game.map.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.task.LinkedTaskSequence;
import com.rs.game.task.Task;
import com.rs.net.encoders.other.Graphics;
import com.rs.plugin.listener.ObjectListener;
import com.rs.plugin.wrapper.ObjectSignature;

/**
 * Lil janky without force movement, but will suffice for now.
 * @author Dennis
 *
 */
@ObjectSignature(objectId = { 2873, 2874, 2875, 2878, 2879 }, name = {})
public class MageBankRegionObjectPlugin extends ObjectListener {

	@Override
	public void execute(Player player, GameObject object, int optionId) throws Exception {
		if (object.getId() == 2873 || object.getId() == 2874 || object.getId() == 2875) {
			if (player.ownsItems(new Item(2412), new Item(2413), new Item(2414))) {
				player.getPackets().sendGameMessage("You already have a God cape.");
				return;
			}
			switch (object.getId()) {
			case 2873:// sara
				process(object, 2412, player);
				break;
			case 2874:// zammy
				process(object, 2414, player);
				break;
			case 2875:// guthix
				process(object, 2413, player);
				break;
			}
		}
		if (object.getId() == 2879 || object.getId() == 2878) {
			player.getMovement().lock(1);
			final WorldTile destination = POOL_DESTINATIONS[2879 - object.getId()];
			player.dialogue(d -> d.mes("You step into the pool of sparkling water. You feel energy rush through your veins."));
			player.task(4, jumper -> {
				jumper.toPlayer().getInterfaceManager().closeInterfaces();
				jumper.toPlayer().getAudioManager().sendSound(Sounds.JUMP_IN_WATER);
				handlePool(jumper.toPlayer(), destination, object);
			});
		}
	}

	private static final WorldTile[] POOL_DESTINATIONS = new WorldTile[] {new WorldTile(2542, 4718, 0), new WorldTile(2509, 4689, 0)};
	
	private static void process(GameObject object, int cape, Player player) {
		String capeType = ItemDefinitions.getItemDefinitions(cape).getName().replace(" cape", "");
		WorldTile capeTile = new WorldTile(player);
		player.setNextAnimation(Animations.PRAYING_TO_ALTAR);
		player.getPackets().sendGameMessage("You kneel and begin to chant to " + capeType + "...");
		player.task(3, p -> p.addWalkSteps(player.getX(), player.getY() - 1));
		World.get().submit(new Task(6) {
			@Override
			public void execute() {
				if (player.ownsItems(new Item(2412), new Item(2413), new Item(2414))) {
					player.getPackets().sendGameMessage("You may only possess one sacred cape at a time. The conflicting powers of the capes drive them apart.");
					return;
				}
				player.setNextFaceWorldTile(object);
				player.dialogue(d -> d
						.mes("You feel a rush of energy through your veins. Suddenly a cape appears before you."));
				player.getPackets().sendGraphics(Graphic.SMOKE_APPEARING, capeTile);
				FloorItem.updateGroundItem(new Item(cape), capeTile, player);
				cancel();
			}
		});
	}
	
	public void handlePool(final Player player, final WorldTile dest, final GameObject object) {
		final WorldTile end = (object.matches(new WorldTile(2508, 4686)) ? new WorldTile(2542, 4718)
				: new WorldTile(2509, 4689));
		final WorldTile middle = object.getId() == 2879 ? new WorldTile(2509, 4687, 0) : new WorldTile(2542, 4720, 0);
		player.getPackets().sendGameMessage("You step into the pool.");
		player.setNextWorldTile(middle);
		player.setNextAnimation(Animations.SG_DRINK_FROM_FOUNTAIN);
		player.getAudioManager().sendSound(Sounds.WATER_SPLASHING);
		player.task(3, p -> p.toPlayer().getPackets().sendGraphics(new Graphics(68), middle));
		LinkedTaskSequence seq = new LinkedTaskSequence();
		seq.connect(1, () -> FadingScreen.fade(player, () -> player.setNextAnimation(Animations.JUMPING_INTO_SOMETHING)));
		seq.connect(1, () -> player.task(2, p -> p.setNextWorldTile(end))).start();
	}
}