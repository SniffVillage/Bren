package nl.sniffiandros.bren.common.registry.custom.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class WorkbenchBlock extends Block {

    public VoxelShape shape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.875, 1));

        return shape;
    }

    public WorkbenchBlock(Settings settings) {
        super(settings.nonOpaque());
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        return this.shape().offset(vec3d.x, vec3d.y, vec3d.z);
    }
}
