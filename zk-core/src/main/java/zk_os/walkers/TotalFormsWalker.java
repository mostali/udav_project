package zk_os.walkers;

import lombok.RequiredArgsConstructor;
import mpu.X;
import org.jetbrains.annotations.NotNull;
import zk_notes.node.NodeDir;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class TotalFormsWalker {

	public static @NotNull Collection<Sdn> findAllPages(Predicate<Sdn> searchNodePredicate, int count) {
		List<Sdn> finded = new LinkedList<>();
		new TotalFormsWalker() {
			@Override
			protected Boolean walkNote(Sdn sdn, NodeDir nodeDir) {
				//nodeDir is null
				if (searchNodePredicate.test(sdn)) {
					finded.add(sdn);
				}
				return finded.size() < count;
			}
		}.withNotes(false).doWalk();
		return finded;
	}


	public static @NotNull List<NodeDir> findAllNotes(Predicate<NodeDir> searchNodePredicate, int count) {
		List<NodeDir> finded = new LinkedList<>();
		new TotalFormsWalker() {
			@Override
			protected Boolean walkNote(Sdn sdn, NodeDir nodeDir) {
				if (searchNodePredicate.test(nodeDir)) {
					finded.add(nodeDir);
				}
				return finded.size() < count;
			}
		}.withNotes(true).doWalk();
		return finded;
	}

	boolean withNotes = true;

	public TotalFormsWalker withNotes(boolean withNotes) {
		this.withNotes = withNotes;
		return this;
	}


	protected abstract Boolean walkNote(Sdn sdn, NodeDir nodeDir);

	public static void main(String[] args) {
		new TotalFormsWalker() {

			@Override
			protected Boolean walkNote(Sdn sdn, NodeDir nodeDir) {
				X.p(sdn + ":" + nodeDir);
				return null;
			}
		};
	}

	public TotalFormsWalker doWalk() {

		PlaneWalker sd3Walker = new PlaneWalker() {

			@Override
			protected Boolean walkPlane(String plane, Path dir, PlaneState planeState) {

				walkPages(plane);

				return true;
			}

			private void walkPages(String sd3) {

				new PagesWalker(sd3) {

					@Override
					protected Boolean walkPage(String pagename, PageState pageState) {

						if (withNotes) {

							walkForms(Sdn.of(_plane, pagename));

						} else {

							walkNote(Sdn.of(_plane, pagename), null);

						}

						return true;
					}


				}.doWalk();

			}

			private void walkForms(Sdn sdn) {
				new NoteWalker(sdn) {

					@Override
					protected Boolean walkForm(NodeDir nodeDir) {
						walkNote((Sdn) sdn, nodeDir);
						return true;
					}
				}.doWalk();
			}

		};
		sd3Walker.withIndex().doWalk();

		return this;

	}
}
