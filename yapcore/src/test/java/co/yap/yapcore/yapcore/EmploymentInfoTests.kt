package co.yap.yapcore.yapcore

import co.yap.app.YAPApplication
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.EmploymentQuestionnaireViewModel
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models.Question
import co.yap.yapcore.enums.EmploymentStatus
import co.yap.yapcore.yapcore.base.BaseTestCase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class EmploymentInfoTests : BaseTestCase() {

    lateinit var sut: EmploymentQuestionnaireViewModel

    data class QuestionnaireItem(
        val employed: List<Question>,
        val selfEmployed: List<Question>
    )

    @Before
    override fun setUp() {
        super.setUp()
        sut = EmploymentQuestionnaireViewModel(YAPApplication())
    }

    @Test
    fun test_no_of_questions_if_employment_status_is_employed() {
        val expectedResult = getQuestionnaire().employed
        val actualResult = sut.questionnaires(EmploymentStatus.EMPLOYED)
        Assert.assertEquals(actualResult.size, expectedResult.size)
    }

    @Test
    fun test_questions_sequence_if_employment_status_is_employed() {
        val expectedResult = getQuestionnaire().employed
        val actualResult = sut.questionnaires(EmploymentStatus.EMPLOYED)
        expectedResult.forEachIndexed { index, question ->
            Assert.assertEquals(question, actualResult[index])
        }
    }

    private fun getQuestionnaire(): QuestionnaireItem {
        val gson = GsonBuilder().create();
        val itemType = object : TypeToken<QuestionnaireItem>() {}.type

        return gson.fromJson<QuestionnaireItem>(readJsonFile(), itemType)
    }

    @Throws(IOException::class)
    private fun readJsonFile(): String? {
        val br =
            BufferedReader(InputStreamReader(FileInputStream("../yapcore/src/main/assets/jsons/employment_questionaires.json")))
        val sb = StringBuilder()
        var line: String? = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        return sb.toString()
    }

}