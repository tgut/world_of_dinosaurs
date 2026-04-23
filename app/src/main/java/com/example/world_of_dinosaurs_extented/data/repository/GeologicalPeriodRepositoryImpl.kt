package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.GeologicalPeriod
import com.example.world_of_dinosaurs_extented.domain.repository.GeologicalPeriodRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of geological periods repository.
 * Data is hardcoded based on paleontological research.
 */
@Singleton
class GeologicalPeriodRepositoryImpl @Inject constructor() : GeologicalPeriodRepository {

    private val periods = listOf(
        GeologicalPeriod(
            era = DinosaurEra.TRIASSIC,
            nameEn = "Triassic Period",
            nameZh = "三叠纪",
            startMya = 252,
            endMya = 201,
            climateEn = "Warm and dry; arid desert conditions, some monsoons",
            climateZh = "温暖干燥；荒漠条件，有季风降雨",
            atmosphereO2Percent = 16.0,
            atmosphereCO2Ppm = 900,
            averageTempC = 24,
            floraDominantEn = listOf("Conifers", "Ferns", "Cycads", "Ginkgos"),
            floraDominantZh = listOf("针叶树", "蕨类", "苏铁", "银杏"),
            faunaPredecessorsEn = listOf("Synapsids (mammal-like reptiles)", "Archosaurs"),
            faunaPredecessorsZh = listOf("合弓纲动物（哺乳类爬行动物）", "主龙形动物"),
            faunaContemporaryEn = listOf("Early dinosaurs (Eoraptor, Coelophysis)", "Flying reptiles (Pterosaurs emerging late)", "Early mammals", "Giant amphibians"),
            faunaContemporaryZh = listOf("早期恐龙（禄丰龙、虚骨龙）", "飞行爬行动物（翼龙晚期出现）", "早期哺乳动物", "巨型两栖动物"),
            faunaSuccessorsEn = listOf("Large dinosaurs", "Dominant archosaurs", "Pterosaurs diversifying"),
            faunaSuccessorsZh = listOf("大型恐龙", "占优势的主龙形动物", "翼龙多样化"),
            majorEventsEn = listOf("Pangaea still intact and breaking apart", "Supercontinent fragmentation begins", "Rise of early archosaurs and dinosaurs"),
            majorEventsZh = listOf("泛大陆仍然完整并开始分裂", "超大陆开始分解", "早期主龙形动物和恐龙兴起"),
            extinctionEventEn = "Triassic-Jurassic extinction (201 Mya) - 75% of species lost",
            extinctionEventZh = "三叠-侏罗灭绝事件（201百万年前）- 75%物种灭绝"
        ),
        GeologicalPeriod(
            era = DinosaurEra.JURASSIC,
            nameEn = "Jurassic Period",
            nameZh = "侏罗纪",
            startMya = 201,
            endMya = 145,
            climateEn = "Warm and humid; tropical to subtropical, with seasonal variations",
            climateZh = "温暖湿润；热带到亚热带，季节变化明显",
            atmosphereO2Percent = 19.0,
            atmosphereCO2Ppm = 600,
            averageTempC = 22,
            floraDominantEn = listOf("Conifers", "Cycads", "Ginkgos", "Ferns", "Early flowering plants (rare)"),
            floraDominantZh = listOf("针叶树", "苏铁", "银杏", "蕨类", "早期被子植物（稀少）"),
            faunaPredecessorsEn = listOf("Small dinosaurs from Triassic", "Early pterosaurs", "Early mammals"),
            faunaPredecessorsZh = listOf("三叠纪小型恐龙", "早期翼龙", "早期哺乳动物"),
            faunaContemporaryEn = listOf("Sauropods (Brachiosaurus, Diplodocus)", "Theropods (Allosaurus, Ceratosaurus)", "Ornithischians (Stegosaurus)", "Flying reptiles (Pterosaurs diverse)", "Marine reptiles (Ichthyosaurs, Plesiosaurs)"),
            faunaContemporaryZh = listOf("蜥脚龙类（腕龙、梁龙）", "兽脚龙类（异特龙、角鼻龙）", "鸟脚龙类（剑龙）", "飞行爬行动物（翼龙多样）", "海生爬行动物（鱼龙、蛇颈龙）"),
            faunaSuccessorsEn = listOf("Large carnivorous theropods", "Massive sauropods declining", "More advanced ornithischians"),
            faunaSuccessorsZh = listOf("大型肉食兽脚龙", "巨型蜥脚龙衰退", "更高等的鸟脚龙"),
            majorEventsEn = listOf("Age of Dinosaurs peak diversity", "Pangaea fully broken into continents", "Extensive shallow seas (Tethys)", "First birds evolving (Archaeopteryx late Jurassic)"),
            majorEventsZh = listOf("恐龙时代多样性顶峰", "泛大陆完全分解成各大陆", "广泛的浅海（特提斯洋）", "第一批鸟类演化（始祖鸟侏罗纪晚期）"),
            extinctionEventEn = null,
            extinctionEventZh = null
        ),
        GeologicalPeriod(
            era = DinosaurEra.CRETACEOUS,
            nameEn = "Cretaceous Period",
            nameZh = "白垩纪",
            startMya = 145,
            endMya = 66,
            climateEn = "Warm greenhouse conditions; no polar ice caps, higher CO2",
            climateZh = "温暖的温室气候；没有极地冰盖，二氧化碳浓度高",
            atmosphereO2Percent = 21.0,
            atmosphereCO2Ppm = 1200,
            averageTempC = 18,
            floraDominantEn = listOf("Flowering plants (Angiosperms) rapidly diversifying", "Conifers still common", "Cycads and ginkgos declining"),
            floraDominantZh = listOf("被子植物（开花植物）快速多样化", "针叶树仍然常见", "苏铁和银杏衰退"),
            faunaPredecessorsEn = listOf("Late Jurassic dinosaurs", "Pterosaurs in decline", "Early birds established"),
            faunaPredecessorsZh = listOf("侏罗纪晚期恐龙", "翼龙衰退", "早期鸟类已建立"),
            faunaContemporaryEn = listOf("Theropods (T. rex, Spinosaurus, Velociraptor)", "Ceratopsians (Triceratops, Iguanodon)", "Ankylosaurs and armored dinosaurs", "Hadrosaurs (duck-billed dinosaurs)", "Flying reptiles (Pterosaurs near extinction)", "Advanced birds", "Marine reptiles (Mosasaurs, Plesiosaurs)"),
            faunaContemporaryZh = listOf("兽脚龙（霸王龙、棘龙、迅猛龙）", "角龙类（三角龙、禽龙）", "甲龙和铠甲恐龙", "鸭嘴龙类", "飞行爬行动物（翼龙接近灭绝）", "高等鸟类", "海生爬行动物（沧龙、蛇颈龙）"),
            faunaSuccessorsEn = listOf("Birds (only dinosaur survivors)", "Mammals (small, diversifying after dinos extinct)"),
            faunaSuccessorsZh = listOf("鸟类（唯一幸存的恐龙）", "哺乳动物（恐龙灭绝后开始多样化）"),
            majorEventsEn = listOf("Flowering plants dominant by end of period", "Continents in modern positions", "Shallow seas and chalk deposits", "Pangaea fully dispersed"),
            majorEventsZh = listOf("被子植物在时期末期占据优势", "大陆接近现代位置", "浅海和白垩沉积", "泛大陆完全分散"),
            extinctionEventEn = "K-Pg extinction event (66 Mya) - Asteroid impact, 75% species lost, dinosaurs extinct",
            extinctionEventZh = "白垩纪-古近纪灭绝事件（66百万年前）- 小行星撞击，75%物种灭绝，恐龙灭绝"
        )
    )

    override suspend fun getPeriodByEra(era: DinosaurEra): GeologicalPeriod? =
        periods.find { it.era == era }

    override suspend fun getAllPeriods(): List<GeologicalPeriod> =
        periods
}
