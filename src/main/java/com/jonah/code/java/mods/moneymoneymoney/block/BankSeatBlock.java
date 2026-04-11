package com.jonah.code.java.mods.moneymoneymoney.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BankSeatBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final String SEAT_TAG = "moneymoneymoney.bank_seat";
    private static final double SEAT_OFFSET_Y = -1.05D;
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D),
            Block.box(3.0D, 3.0D, 3.0D, 13.0D, 9.0D, 13.0D),
            Block.box(3.0D, 9.0D, 11.0D, 13.0D, 16.0D, 14.0D));

    public BankSeatBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(1.5F)
                .sound(SoundType.WOOD)
                .noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSecondaryUseActive() || player.isPassenger()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ArmorStand seat = getOrCreateSeat((ServerLevel) level, pos, state.getValue(FACING));
        if (seat == null || !seat.getPassengers().isEmpty()) {
            return InteractionResult.CONSUME;
        }

        player.startRiding(seat, false);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            removeSeat(serverLevel, pos);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    private static ArmorStand getOrCreateSeat(ServerLevel level, BlockPos pos, Direction facing) {
        List<ArmorStand> seats = level.getEntitiesOfClass(ArmorStand.class,
                Shapes.block().bounds().move(pos).inflate(0.25D),
                seat -> seat != null && seat.isAlive() && seat.getTags().contains(SEAT_TAG) && pos.equals(seat.blockPosition()));
        if (!seats.isEmpty()) {
            return seats.get(0);
        }

        ArmorStand seat = new ArmorStand(level, pos.getX() + 0.5D, pos.getY() + SEAT_OFFSET_Y, pos.getZ() + 0.5D);
        seat.addTag(SEAT_TAG);
        seat.setInvisible(true);
        seat.setInvulnerable(true);
        seat.setNoGravity(true);
        seat.setSilent(true);
        seat.setNoBasePlate(true);
        seat.moveTo(pos.getX() + 0.5D, pos.getY() + SEAT_OFFSET_Y, pos.getZ() + 0.5D, facing.toYRot(), 0.0F);
        level.addFreshEntity(seat);
        return seat;
    }

    private static void removeSeat(ServerLevel level, BlockPos pos) {
        List<ArmorStand> seats = level.getEntitiesOfClass(ArmorStand.class,
                Shapes.block().bounds().move(pos).inflate(0.25D),
                EntitySelector.ENTITY_STILL_ALIVE.and(seat -> seat.getTags().contains(SEAT_TAG) && pos.equals(seat.blockPosition())));
        for (ArmorStand seat : seats) {
            seat.discard();
        }
    }
}
