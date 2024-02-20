package com.hisun.codeassistant.llms.client.openai.completion;

import com.hisun.codeassistant.llms.completion.CompletionModel;

import java.util.Arrays;

public enum OpenAIChatCompletionModel implements CompletionModel {
    GPT_3_5_0125_16k("gpt-3.5-turbo-0125", "GPT-3.5 Turbo (16k)", 16384),
    GPT_4_0125_128k("gpt-4-0125-preview", "GPT-4 Turbo (128k)", 128000);

    private final String code;
    private final String description;
    private final int maxTokens;

    OpenAIChatCompletionModel(String code, String description, int maxTokens) {
        this.code = code;
        this.description = description;
        this.maxTokens = maxTokens;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    @Override
    public String toString() {
        return description;
    }

    public static OpenAIChatCompletionModel findByCode(String code) {
        return Arrays.stream(OpenAIChatCompletionModel.values())
                .filter(item -> item.getCode().equals(code))
                .findFirst().orElseThrow();
    }
}
