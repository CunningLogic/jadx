package jadx.core.dex.attributes;

import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.Edge;
import jadx.core.utils.BlockUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LoopAttr implements IAttribute {

	private final BlockNode start;
	private final BlockNode end;
	private final Set<BlockNode> loopBlocks;

	public LoopAttr(BlockNode start, BlockNode end) {
		this.start = start;
		this.end = end;
		this.loopBlocks = Collections.unmodifiableSet(BlockUtils.getAllPathsBlocks(start, end));
	}

	public BlockNode getStart() {
		return start;
	}

	public BlockNode getEnd() {
		return end;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.LOOP;
	}

	public Set<BlockNode> getLoopBlocks() {
		return loopBlocks;
	}

	/**
	 * Return source blocks of exit edges. <br>
	 * Exit nodes belongs to loop (contains in {@code loopBlocks})
	 */
	public Set<BlockNode> getExitNodes() {
		Set<BlockNode> nodes = new HashSet<BlockNode>();
		Set<BlockNode> blocks = getLoopBlocks();
		for (BlockNode block : blocks) {
			// exit: successor node not from this loop, (don't change to getCleanSuccessors)
			for (BlockNode s : block.getSuccessors()) {
				if (!blocks.contains(s) && !s.getAttributes().contains(AttributeType.EXC_HANDLER)) {
					nodes.add(block);
				}
			}
		}
		return nodes;
	}

	/**
	 * Return loop exit edges.
	 */
	public List<Edge> getExitEdges() {
		List<Edge> edges = new LinkedList<Edge>();
		Set<BlockNode> blocks = getLoopBlocks();
		for (BlockNode block : blocks) {
			for (BlockNode s : block.getSuccessors()) {
				if (!blocks.contains(s) && !s.getAttributes().contains(AttributeType.EXC_HANDLER)) {
					edges.add(new Edge(block, s));
				}
			}
		}
		return edges;
	}

	@Override
	public String toString() {
		return "LOOP: " + start + "->" + end;
	}
}
