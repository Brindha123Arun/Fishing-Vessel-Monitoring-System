package fr.gouv.cnsp.monitorfish.infrastructure.api.bff

import fr.gouv.cnsp.monitorfish.domain.use_cases.control_objective.*
import fr.gouv.cnsp.monitorfish.infrastructure.api.input.UpdateControlObjectiveDataInput
import fr.gouv.cnsp.monitorfish.infrastructure.api.outputs.ControlObjectiveDataOutput
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.websocket.server.PathParam
import fr.gouv.cnsp.monitorfish.infrastructure.api.input.AddControlObjectiveDataInput as AddControlObjectiveDataInput1

@RestController
@RequestMapping("/bff/v1/control_objectives")
@Api(description = "APIs for Control objectives")
class ControlObjectiveController(
    private val getControlObjectivesOfYear: GetControlObjectivesOfYear,
    private val getControlObjectiveYearEntries: GetControlObjectiveYearEntries,
    private val addControlObjectiveYear: AddControlObjectiveYear,
    private val updateControlObjective: UpdateControlObjective,
    private val deleteControlObjective: DeleteControlObjective,
    private val addControlObjective: AddControlObjective
) {

    @GetMapping("/{year}")
    @ApiOperation("Get control objectives of a given year")
    fun getControlObjectivesOfYear(
        @PathParam("Year")
        @PathVariable(name = "year")
        year: Int
    ): List<ControlObjectiveDataOutput> {
        return getControlObjectivesOfYear.execute(year).map { controlObjective ->
            ControlObjectiveDataOutput.fromControlObjective(controlObjective)
        }
    }

    @GetMapping("/years")
    @ApiOperation("Get control objective year entries")
    fun getControlObjectiveYearEntries(): List<Int> {
        return getControlObjectiveYearEntries.execute()
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/years")
    @ApiOperation("Add a control objective year")
    fun addControlObjectiveYear() {
        return addControlObjectiveYear.execute()
    }

    @PutMapping(value = ["/{controlObjectiveId}"], consumes = ["application/json"])
    @ApiOperation("Update a control objective")
    fun updateControlObjective(
        @PathParam("Control objective id")
        @PathVariable(name = "controlObjectiveId")
        controlObjectiveId: Int,
        @RequestBody
        updateControlObjectiveData: UpdateControlObjectiveDataInput
    ) {
        updateControlObjective.execute(
            id = controlObjectiveId,
            targetNumberOfControlsAtSea = updateControlObjectiveData.targetNumberOfControlsAtSea,
            targetNumberOfControlsAtPort = updateControlObjectiveData.targetNumberOfControlsAtPort,
            controlPriorityLevel = updateControlObjectiveData.controlPriorityLevel
        )
    }

    @DeleteMapping(value = ["/{controlObjectiveId}"])
    @ApiOperation("Delete a control objective")
    fun deleteControlObjective(
        @PathParam("Control objective id")
        @PathVariable(name = "controlObjectiveId")
        controlObjectiveId: Int
    ) {
        deleteControlObjective.execute(controlObjectiveId)
    }

    @PostMapping(value = [""], consumes = ["application/json"])
    @ApiOperation("Add a control objective")
    fun addControlObjective(
        @RequestBody
        addControlObjectiveData: AddControlObjectiveDataInput1
    ): Int {
        return addControlObjective.execute(
            segment = addControlObjectiveData.segment,
            facade = addControlObjectiveData.facade,
            year = addControlObjectiveData.year
        )
    }
}
