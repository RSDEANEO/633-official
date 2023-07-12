package skills.crafting;

import java.util.Optional;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.constants.Sounds;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.net.encoders.other.Animation;
import com.rs.utilities.TextUtils;

import skills.ProducingSkillAction;
import skills.Skills;

public final class PotteryOven extends ProducingSkillAction {
	
	/**
	 * The data this skill action is dependent of.
	 */
	private final PotteryData data;
	
	/**
	 * The amount of times this task should run for.
	 */
	private int amount;
	
	/**
	 * Constructs a new {@link PotteryOven}.
	 * @param player {@link #getPlayer()}.
	 * @param data {@link #data}.
	 * @param amount {@link #amount}.
	 */
	public PotteryOven(Player player, PotteryData data, int amount) {
		super(player, Optional.empty());
		this.data = data;
		this.amount = amount;
	}

	@Override
	public void onProduce(Task t, boolean success) {
		if(success) {
			amount--;
			player.getAudioManager().sendSound(Sounds.LOOM_WEAVING);
			player.getDetails().getStatistics().addStatistic(ItemDefinitions.getItemDefinitions(data.produced).getName() + "_Loomed").addStatistic("Items_Loomed");
			if(amount <= 0)
				t.cancel();
		}
	}
	
	@Override
	public Optional<Animation> animation() {
		return Optional.of(new Animation(883));
	}
	
	@Override
	public Optional<Item[]> removeItem() {
		return Optional.of(new Item[]{new Item(data.item)});
	}
	
	@Override
	public Optional<Item[]> produceItem() {
		return Optional.of(new Item[]{new Item(data.produced)});
	}
	
	@Override
	public int delay() {
		return 5;
	}
	
	@Override
	public boolean instant() {
		return true;
	}
	
	@Override
	public boolean initialize() {
		player.getInterfaceManager().closeInterfaces();
		return checkCrafting();
	}
	
	@Override
	public boolean canExecute() {
		return checkCrafting();
	}
	
	@Override
	public double experience() {
		return data.experience;
	}
	
	@Override
	public int getSkillId() {
		return Skills.CRAFTING;
	}
	
	@Override
	public void onStop() {
	}
	
	private boolean checkCrafting() {
		if(player.getSkills().getLevel(Skills.CRAFTING) < data.requirement) {
			player.getPackets().sendGameMessage("You need a crafting level of " + data.requirement + " to spin " + TextUtils.appendIndefiniteArticle(ItemDefinitions.getItemDefinitions(data.item).getName()));
			return false;
		}
		return true;
	}
	
	public enum PotteryData {
		POT(1787, 6, 1931, 1, 6.3),
		PIE_DISH(1789, 4, 2313, 7, 10.0),
		BOWL(1791, 4, 1923, 8, 15.0),
		;
		/**
		 * The item required to spin.
		 */
		public final int item;
		
		/**
		 * The amount of the item requried.
		 */
		public final int amount;
		
		/**
		 * The item produced from spinning.
		 */
		public final int produced;
		
		/**
		 * The requirement to spin.
		 */
		public final int requirement;
		
		/**
		 * The experience gained from spinning.
		 */
		private final double experience;
		
		/**
		 * Constructs a new {@link PotteryData}.
		 * @param item {@link #item}.
		 * @param produced {@link #produced}.
		 * @param requirement {@link #requirement}.
		 * @param experience {@link #experience}.
		 */
		PotteryData(int item, int amount, int produced, int requirement, double experience) {
			this.item = item;
			this.amount = amount;
			this.produced = produced;
			this.requirement = requirement;
			this.experience = experience;
		}
	}
}