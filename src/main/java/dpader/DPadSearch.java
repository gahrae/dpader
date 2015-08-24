package dpader;

import java.util.ArrayList;
import java.util.List;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.Transition;
import es.usc.citius.hipster.model.function.ActionFunction;
import es.usc.citius.hipster.model.function.ActionStateTransitionFunction;
import es.usc.citius.hipster.model.function.CostFunction;
import es.usc.citius.hipster.model.function.HeuristicFunction;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.ProblemBuilder;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.Predicate;

public class DPadSearch {

	private DPadSearch() {
	}

	public static Algorithm<DPadAction, DPad, WeightedNode<DPadAction, DPad, Double>>.SearchResult search(
			String message) {
		DPad initialState = new DPad();
		return search(initialState, message);
	}

	public static Algorithm<DPadAction, DPad, WeightedNode<DPadAction, DPad, Double>>.SearchResult search(
			DPad initialState, String message) {

		message = encodeMessage(message);

		if (!allCharactersReachable(initialState, message)) {
			throw new IllegalArgumentException(
					"Message contains characters that are not reachable in dpad.");
		}

		SearchProblem<DPadAction, DPad, WeightedNode<DPadAction, DPad, Double>> problem = ProblemBuilder
				.create().initialState(initialState)
				.defineProblemWithExplicitActions()
				.useActionFunction(getActionFunction(message))
				.useTransitionFunction(getActionTransitionFunction())
				.useCostFunction(getCostFunction())
				.useHeuristicFunction(getHeuristicFunction(message)).build();

		Predicate<WeightedNode<DPadAction, DPad, Double>> predicate = getPredicateFunction(message);

		return Hipster.createAStar(problem).search(predicate);
	}

	public static String encodeMessage(String message) {
		return message.replace(' ', '_') + "=";
	}

	public static boolean allCharactersReachable(DPad dpad, String message) {
		DPad copyOfDpad = new DPad(dpad);
		for (char c : message.toCharArray()) {
			if (copyOfDpad.getLocationOf(c) == null) {
				copyOfDpad.toggleCase();
				if (copyOfDpad.getLocationOf(c) == null) {
					// System.out.println("Couldn't find " + c);
					return false;
				}
			}
		}
		return true;
	}

	public static ActionFunction<DPadAction, DPad> getActionFunction(String goal) {
		ActionFunction<DPadAction, DPad> af = new ActionFunction<DPadAction, DPad>() {
			public Iterable<DPadAction> actionsFor(DPad state) {
				List<DPadAction> actionList = new ArrayList<DPadAction>();
				actionList.add(DPadAction.MOVE_UP);
				actionList.add(DPadAction.MOVE_DOWN);
				actionList.add(DPadAction.MOVE_LEFT);
				actionList.add(DPadAction.MOVE_RIGHT);

				Character nextKey = state.nextKey(goal);
				if (nextKey != null) {

					if (nextKey == state.getCursorKey())
						actionList.add(DPadAction.PRESS);

					boolean capsLockOn = state.isCapsLockOn();
					boolean nextKeyIsCaptial = Character.isUpperCase(nextKey);
					boolean keyIsReachableThroughCaseChange = state.isCaseChangeKey(nextKey);
					boolean wrongCase = (capsLockOn && !nextKeyIsCaptial)
							|| (!capsLockOn && nextKeyIsCaptial)
							|| keyIsReachableThroughCaseChange;

					boolean noStrategyInUse = state.getStrategy() == DPadStrategy.NONE;
					if (wrongCase && noStrategyInUse) {
						actionList.add(DPadAction.STRATEGY_TOGGLE_CAPSLOCK);
						actionList.add(DPadAction.STRATEGY_TOGGLE_SHIFT);
					}
				}
				return actionList;
			}
		};
		return af;
	}

	public static ActionStateTransitionFunction<DPadAction, DPad> getActionTransitionFunction() {
		ActionStateTransitionFunction<DPadAction, DPad> atf = new ActionStateTransitionFunction<DPadAction, DPad>() {
			public DPad apply(DPadAction action, DPad state) {
				DPad next = new DPad(state);

				if (action == DPadAction.MOVE_UP
						|| action == DPadAction.MOVE_DOWN
						|| action == DPadAction.MOVE_LEFT
						|| action == DPadAction.MOVE_RIGHT) {
					next.move(action);
				} else if (action == DPadAction.PRESS) {
					next.press();
				} else if (action == DPadAction.STRATEGY_TOGGLE_CAPSLOCK) {
					next.setStrategy(DPadStrategy.CAPSLOCK);
				} else if (action == DPadAction.STRATEGY_TOGGLE_SHIFT) {
					next.setStrategy(DPadStrategy.SHIFT);
				}

				return next;
			}
		};
		return atf;
	}

	public static CostFunction<DPadAction, DPad, Double> getCostFunction() {
		CostFunction<DPadAction, DPad, Double> cf = new CostFunction<DPadAction, DPad, Double>() {
			public Double evaluate(Transition<DPadAction, DPad> transition) {
				return 1d;
			}
		};
		return cf;
	}

	public static HeuristicFunction<DPad, Double> getHeuristicFunction(
			String goal) {
		HeuristicFunction<DPad, Double> hf = new HeuristicFunction<DPad, Double>() {
			public Double estimate(DPad state) {

				double cursorDistanceFromNextLetter = cursorDistanceFromNextLetter(state, goal);
				double matchScore = countInitialMatchingLetters(state.getMessage(), goal);

				double heuristicValue = (matchScore * 1000) + cursorDistanceFromNextLetter;

				// System.out.println("h: " + state
				// + " h=" + heuristicValue
				// + " nextKey=" + state.nextKey(goalMessage)
				// + " keyUnder=" + state.getCursorKey()
				// + " strategy=" + state.getStrategy()
				// + " isCaps=" + state.isCapsLockOn()
				// + " hash=" + state.hashCode()
				// );

				return heuristicValue;
			}

			private double cursorDistanceFromNextLetter(DPad state,
					String goalMessage) {

				Character nextChar = state.nextKey(goalMessage);
				if (nextChar != null) {
					int distance = state.getKeyDistance(nextChar);
					boolean unknownCharacter = distance == -1;
					if (!unknownCharacter) {
						return distance;
					}
				}

				return state.getPadWidth() * state.getPadHeight();
			}

			public int countInitialMatchingLetters(String current, String goal) {
				int matchScore = 0;
				int limit = Math.min(current.length(), goal.length());
				for (int i = 0; i < limit; i++) {
					if (current.charAt(i) != goal.charAt(i)) {
						break;
					}
					matchScore++;
				}
				return goal.length() - matchScore;
			}
		};
		return hf;
	}

	public static Predicate<WeightedNode<DPadAction, DPad, Double>> getPredicateFunction(
			String goal) {
		Predicate<WeightedNode<DPadAction, DPad, Double>> predicate = new Predicate<WeightedNode<DPadAction, DPad, Double>>() {
			public boolean apply(WeightedNode<DPadAction, DPad, Double> input) {

				String message = input.state().getMessage();
				boolean match = message.equals(goal);

				// System.out.println("Predicate: " + message + "=" + goalMessage + "? " + match);

				return match;
			}
		};
		return predicate;
	}
}
