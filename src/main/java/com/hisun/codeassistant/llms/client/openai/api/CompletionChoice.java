package com.hisun.codeassistant.llms.client.openai.api;

import lombok.Data;

@Data
public class CompletionChoice {
    /**
     * The generated text. Will include the prompt if {@link CompletionRequest#echo } is true
     */
    String text;

    /**
     * This index of this completion in the returned list.
     */
    Integer index;

    /**
     * The log probabilities of the chosen tokens and the top {@link CompletionRequest#logprobs} tokens
     */
    LogProbResult logprobs;

    /**
     * The reason why GPT stopped generating, for example "length".
     */
    String finish_reason;
}
