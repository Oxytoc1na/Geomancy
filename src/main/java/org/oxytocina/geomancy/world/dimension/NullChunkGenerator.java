package org.oxytocina.geomancy.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.NullSpikeBlock;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.util.GenUtil;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NullChunkGenerator extends ChunkGenerator {
    public static final Codec<NullChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(NullChunkGenerator::getBiomeSource),
                    Codec.INT.fieldOf("world_height").forGetter(NullChunkGenerator::getWorldHeight)
                    //Identifier.CODEC.fieldOf("custom_block").forGetter(NullChunkGenerator::getCustomBlockID)
            ).apply(instance, NullChunkGenerator::new));

    /* you can add whatever fields you want to this constructor, as long as they're added to the codec as well */
    public NullChunkGenerator(BiomeSource biomeSource, int worldHeight) {
        super(biomeSource);
        this.worldHeight = worldHeight;
        //this.customBlock = Registries.BLOCK.getOrThrow(RegistryKey.of(RegistryKeys.BLOCK, defaultBlock)).getDefaultState();
        //this.customBlockID = customBlockID; // this line is included because we need to have a getter for the ID specifically
    }

    /* the method that creates non-noise caves (i.e., all the caves we had before the caves and cliffs update) */
    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

    }

    /* the method that places grass, dirt, and other things on top of the world, as well as handling the bedrock and deepslate layers,
    as well as a few other miscellaneous things. without this method, your world is just a blank stone (or whatever your default block is) canvas (plus any ores, etc) */


    static final BlockState rubbleState = ModBlocks.NULL_RUBBLE.getDefaultState();
    static final BlockState crystalState = ModBlocks.NULL_CRYSTAL.getDefaultState();
    static final BlockState spikeUpState = ModBlocks.NULL_SPIKE.getDefaultState().with(NullSpikeBlock.VERTICAL_DIRECTION, Direction.UP);
    static final BlockState spikeDownState = ModBlocks.NULL_SPIKE.getDefaultState().with(NullSpikeBlock.VERTICAL_DIRECTION, Direction.DOWN);

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        int height = chunk.getHeight();

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        // generate noise
        int genX, genY, genZ;
        final float blobThreshold = 0.9f;
        final float groundSpikeThreshold = 0.7f;
        final float ceilingSpikeThreshold = 0.6f;
        final float surfaceThreshold = 0.8f;
        final float surfaceNoisePerDepth = 0.02f;
        BlockState state = null;
        for(int ix = 0; ix < 16; ++ix) {
            genX = startX+ix;
            for(int iy = 0; iy < height; ++iy) {
                genY = iy;
                for(int iz = 0; iz < 16; ++iz) {
                    genZ = startZ+iz;
                    state = chunk.getBlockState(mutable.set(ix, iy, iz));
                    // null rubble
                    if(state.isIn(ModBlockTags.NULL_RUBBLE_REPLACEABLE))
                    {
                        // rubble blobs
                        float noise = rubbleNoise(genX,genY,genZ);
                        if(noise > blobThreshold)
                            chunk.setBlockState(mutable, rubbleState, false);

                        // surface
                        if(noise > surfaceThreshold)
                        {
                            int depth = Math.round((float)Math.ceil((noise-surfaceThreshold)/surfaceNoisePerDepth));
                            var topstate = chunk.getBlockState(mutable.up(depth));
                            mutable.down(depth);
                            if(topstate.isAir())
                            {
                                chunk.setBlockState(mutable, rubbleState, false);
                            }
                        }
                    }

                    // null crystal
                    if(state.isIn(ModBlockTags.NULL_CRYSTAL_REPLACEABLE))
                    {
                        // rubble blobs
                        float noise = crystalNoise(genX,genY,genZ);
                        if(noise > blobThreshold)
                            chunk.setBlockState(mutable, crystalState, false);

                        // surface
                        if(noise > surfaceThreshold)
                        {
                            int depth = Math.round((float)Math.ceil((noise-surfaceThreshold)/surfaceNoisePerDepth));
                            var bottomstate = chunk.getBlockState(mutable.down(depth));
                            var immediateBottomState = chunk.getBlockState(mutable.down());
                            if(bottomstate.isAir())
                            {
                                chunk.setBlockState(mutable, crystalState, false);
                            }

                            // "paint" layer onto surface
                            if(immediateBottomState.isAir()){
                                int offset = 0;
                                while(immediateBottomState.isAir() && mutable.getY()-(++offset)>chunk.getBottomY()){
                                    immediateBottomState = chunk.getBlockState(mutable.down(offset));
                                }
                                if(immediateBottomState.isIn(ModBlockTags.NULL_CRYSTAL_REPLACEABLE)){
                                    chunk.setBlockState(mutable.down(offset), crystalState, false);
                                }
                            }
                        }
                    }

                    // spikes on ground
                    if(genY < 64 && state.isIn(ModBlockTags.NULL_HOLDS_SPIKES))
                    {
                        var upperState = chunk.getBlockState(mutable.up());
                        if(upperState.isAir()){
                            // theres space!
                            float noise = groundSpikeNoise(genX,genY,genZ);
                            noise = Toolbox.clampF(noise-((iy-30)*0.02f),0,noise);
                            if(noise > groundSpikeThreshold)
                                chunk.setBlockState(mutable.up(), spikeUpState, false);
                        }
                    }

                    // spikes on ceiling
                    if(state.isIn(ModBlockTags.NULL_HOLDS_SPIKES))
                    {
                        var lowerState = chunk.getBlockState(mutable.down());
                        if(lowerState.isAir()){
                            // theres space!
                            float noise = groundSpikeNoise(genX,genY,genZ);
                            noise = Toolbox.clampF(noise+((iy-height+100)*0.02f),0,noise);
                            if(noise > ceilingSpikeThreshold)
                                chunk.setBlockState(mutable.down(), spikeDownState, false);
                        }
                    }

                }
            }
        }
    }
    /* the method that paints biomes on top of the already-generated terrain. if you leave this method alone, the entire world will be a River biome.
     note that this does not mean that the world will all be water; but drowned and salmon will spawn. */
    @Override
    public CompletableFuture<Chunk> populateBiomes(Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return super.populateBiomes(executor, noiseConfig, blender, structureAccessor, chunk);
    }

    /* this method spawns entities in the world */
    @Override
    public void populateEntities(ChunkRegion region) {
    }

    /* the distance between the highest and lowest points in the world. in vanilla, this is 384 (64+320) */
    final int worldHeight;
    @Override
    public int getWorldHeight() {
        return worldHeight;
    }

    /* this method builds the shape of the terrain. it places stone everywhere, which will later be overwritten with grass, terracotta, snow, sand, etc
     by the buildSurface method. it also is responsible for putting the water in oceans. it returns a CompletableFuture-- you'll likely want this to be delegated to worker threads. */
    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(
                Util.debugSupplier("wgen_fill_noise", () ->
                        this.generate(chunk)),
                Util.getMainWorkerExecutor()).whenCompleteAsync((chunkx, throwable) -> {},
                executor);
    }

    private Chunk generate(Chunk chunk){
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        int height = getWorldHeight();

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Heightmap oceanFloorHeightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap surfaceHeightmap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        // generate noise
        int genX, genY, genZ;
        final float thresholdNoiseScale = 0.004f;
        final float padding = 0.05f; // padding in percent from bottom and top
        BlockState core = ModBlocks.NULL_ROCK.getDefaultState();
        for(int ix = 0; ix < 16; ++ix) {
            genX = ((startX+ix)/2)*2;
            for(int iy = 0; iy < height; ++iy) {
                genY = (iy/2)*2;
                for(int iz = 0; iz < 16; ++iz) {
                    genZ = ((startZ+iz)/2)*2;
                    float distanceToEnds =  Math.min(iy,height-iy)/(float)height;

                    float threshold = Toolbox.clampF((distanceToEnds - padding)*6,0,1)
                            * (0.2f + SimplexNoise.noiseNormalized(genX,genY,genZ,thresholdNoiseScale)*0.6f);

                    float noise = tunnellyNoise(genX,iy,genZ);

                    if(noise > threshold)
                        chunk.setBlockState(mutable.set(ix, iy, iz), core, false);
                }
            }
        }

        // generate maze
        final float typeNoiseScale = 0.00742f;
        final float typeMazeThreshold = 0.1f;
        final float typeNoise =
                SimplexNoise.noiseNormalized(startX*typeNoiseScale,0,startZ*typeNoiseScale)
                ;
        if(typeNoise<typeMazeThreshold)
        {
            // generate cells
            var sections = chunk.getSectionArray();
            for(int i = 0; i < sections.length; i++){
                var section = sections[i];
                section.lock();
                generateMazeSection(chunk,section,i);
                section.unlock();
            }
        }

        // generate floor and roof
        int bottomY = chunk.getBottomY();
        int topY = chunk.getTopY()-1;
        BlockState bread = Blocks.BEDROCK.getDefaultState();
        for(int ix = 0; ix < 16; ++ix) {
            for(int iz = 0; iz < 16; ++iz) {
                chunk.setBlockState(mutable.set(ix, bottomY, iz), bread, false);
                chunk.setBlockState(mutable.set(ix, topY, iz), bread, false);
                oceanFloorHeightmap.trackUpdate(ix,bottomY,iz, bread);
                surfaceHeightmap.trackUpdate(ix,topY,iz, bread);
            }
        }

        return chunk;
    }

    final static float mainNoiseScale = 0.03f;
    final static float mainNoiseScaleOctave1 = 0.1431f;
    final static float mainNoiseScaleOctave2 = 0.028314f;
    private float tunnellyNoise(int x, int y, int z){
        return (0.8f * ((1-(float)Math.pow(SimplexNoise.noiseNormalized(x*mainNoiseScale,y*mainNoiseScale,z*mainNoiseScale),3))*3)%1
                + 0.2f * SimplexNoise.noiseNormalized(x,y,z,mainNoiseScaleOctave1))
                * Toolbox.clampF(
                    20*(SimplexNoise.noiseNormalized(x,y,z,mainNoiseScaleOctave2)-0.5f)
                        ,0.2f,1)
        ;
    }

    private float rubbleNoise(int x, int y, int z){
        return 0.8f * (1-(float)Math.pow(SimplexNoise.noiseNormalized(x*mainNoiseScale+0.51231,y*mainNoiseScale+0.3142,z*mainNoiseScale+0.1573),2))%1
                + 0.2f * SimplexNoise.noiseNormalized(x+4819,y+1931,z-5738,mainNoiseScaleOctave1)
                ;
    }

    final static float crystalNoiseScale = 0.041f;
    final static float crystalNoiseScaleOctave1 = 0.1331f;
    private float crystalNoise(int x, int y, int z){
        return 0.7f * (1-(float)Math.pow(SimplexNoise.noiseNormalized(x* crystalNoiseScale +0.51231,y* crystalNoiseScale +0.3142,z* crystalNoiseScale +0.1573),2))%1
                + 0.3f * SimplexNoise.noiseNormalized(x+623,y+4375,z-165, crystalNoiseScaleOctave1)
                ;
    }

    final static float spikeNoiseScale = 0.3524f;
    private float groundSpikeNoise(int x, int y, int z){
        return 1 * (1-(float)Math.pow(SimplexNoise.noiseNormalized(x* spikeNoiseScale +0.51231,y* spikeNoiseScale +0.3142,z* spikeNoiseScale +0.1573),2))%1
                ;
    }

    private ChunkSection generateMazeSection(Chunk chunk, ChunkSection section, int y){
        BlockState wall = Blocks.DEEPSLATE_BRICKS.getDefaultState();
        BlockState wireframe = Blocks.STONE.getDefaultState();
        Random random = new Random(chunk.getPos().hashCode()+y*1000);
        final float wallChance = 0.7f;
        boolean[] directions = new boolean[]{
                    random.nextFloat() < wallChance,
                    random.nextFloat() < wallChance,
                    random.nextFloat() < wallChance,
                    random.nextFloat() < wallChance,
                    random.nextFloat() < wallChance,
                    random.nextFloat() < wallChance
        };

        final int width = 16;
        final int height = 16;
        final int depth = 16;
        final int yOff = y*16;

        if(directions[0]) // bottom
            GenUtil.fillBox(chunk,wall,1,yOff+0,1,width-1,yOff+0,depth-1);
        if(directions[1]) // top
            GenUtil.fillBox(chunk,wall,1,yOff+height-1,1,width-1,yOff+height-1,depth-1);
        if(directions[2]) // left
            GenUtil.fillBox(chunk,wall,0,yOff+1,1,0,yOff+height-1,depth-1);
        if(directions[3]) // right
            GenUtil.fillBox(chunk,wall,width-1,yOff+1,1,width-1,yOff+height-1,depth-1);
        if(directions[4]) // back
            GenUtil.fillBox(chunk,wall,1,yOff+1,0,width-1,yOff+height-1,0);
        if(directions[5]) // front
            GenUtil.fillBox(chunk,wall,1,yOff+1,depth-1,width-1,yOff+height-1,depth-1);

        // wireframe
        GenUtil.fillKeepWireframe(chunk,wireframe,0,yOff,0,width-1,yOff+height-1,depth-1);

        return section;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    /* the lowest value that blocks can be placed in the world. in a vanilla world, this is -64. */
    @Override
    public int getMinimumY() {
        return 0;
    }

    /* this method returns the height of the terrain at a given coordinate. it's used for structure generation */
    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 0;
    }

    /* this method returns a "core sample" of the world at a given coordinate. it's used for structure generation */
    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    /* this method adds text to the f3 menu. for NoiseChunkGenerator, it's the NoiseRouter line */
    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}