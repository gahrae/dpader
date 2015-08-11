package dpader;

import java.util.Collections;
import java.util.List;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.model.impl.WeightedNode;

/**
 * Solution to Golf Code challenge using A Star Search algorithm.
 * 
 * Golf Code challenge:
 * http://codegolf.stackexchange.com/questions/53805/enter-your-name-via-a-d-pad
 * 
 * Java search algorithm library "Hipster": http://www.hipster4j.org/
 * 
 * @author Gareth S.
 */

public class DPader {

	public static void main(String[] args) {

		if (args.length != 1) {
			displayUsage();
			return;
		}

		// String message = "Code Golf";
		String message = args[0];

		Algorithm<DPadAction, DPad, WeightedNode<DPadAction, DPad, Double>>.SearchResult result = DPadSearch.search(message);
		displayResult(result, message);
	}

	public static void displayUsage() {
		System.out.println("java DPader \"a message\"");
	}

	public static void displayResult(
			Algorithm<DPadAction, DPad, WeightedNode<DPadAction, DPad, Double>>.SearchResult result,
			String message) {

		String resultMessage = result.getGoalNode().state().getMessage();
		boolean solutionFound = DPadSearch.encodeMessage(message).equals(
				resultMessage);

		if (solutionFound) {

			List<WeightedNode<DPadAction, DPad, Double>> path = result
					.getGoalNode().path();
			Collections.reverse(path);

			Character currentKey = null;
			int moveTransitionSum = 0;
			int moveSum = 0;
			int userActionSum = 0;
			int totalActionSum = 0;
			for (WeightedNode<DPadAction, DPad, Double> step : path) {
				if (currentKey == null) {
					currentKey = step.state().getCursorKey();
					continue;
				}

				DPadAction action = step.action();
				boolean isPresing = action == DPadAction.PRESS;
				if (isPresing) {
					Character nextKey = step.previousNode().state().getCursorKey();
					System.out.println(currentKey 
							+ " -> " + nextKey 
							+ " = " + moveTransitionSum);

					currentKey = nextKey;
					moveTransitionSum = 0;
				}

				if (action == DPadAction.MOVE_DOWN
						|| action == DPadAction.MOVE_LEFT
						|| action == DPadAction.MOVE_UP
						|| action == DPadAction.MOVE_RIGHT) {
					moveSum++;
					moveTransitionSum++;
				}

				if (action == DPadAction.MOVE_DOWN
						|| action == DPadAction.MOVE_LEFT
						|| action == DPadAction.MOVE_UP
						|| action == DPadAction.MOVE_RIGHT
						|| action == DPadAction.PRESS) {
					userActionSum++;
				}

				totalActionSum++;

			}

			System.out.println();
			System.out.println("Total 'movement' actions = " + moveSum);
			System.out.println("Total 'user' actions = " + userActionSum);
			System.out.println("Total actions = " + totalActionSum);

			System.out.println();
			System.out.println("Additional details...");
			System.out.println(result);
		} else {
			System.out.println("No solution found");
		}

	}
}