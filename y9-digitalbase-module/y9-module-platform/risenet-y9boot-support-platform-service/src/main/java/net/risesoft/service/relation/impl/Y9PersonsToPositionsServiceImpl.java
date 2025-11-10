package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9PersonsToPositionsServiceImpl implements Y9PersonsToPositionsService {

    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;

    private final Y9PersonManager y9PersonManager;

    @Override
    @Transactional
    public List<PersonsPositions> addPersons(String positionId, String[] personIds) {
        List<Y9PersonsToPositions> personsToPositionsList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {
            String personId = personIds[i];
            if (y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId).isPresent()) {
                continue;
            }
            personsToPositionsList.add(y9PersonsToPositionsManager.save(personId, positionId));
        }
        return entityToModel(personsToPositionsList);
    }

    @Override
    @Transactional
    public List<PersonsPositions> addPositions(String personId, String[] positionIds) {
        List<Y9PersonsToPositions> personsToPositionsList = new ArrayList<>();
        for (int i = 0; i < positionIds.length; i++) {
            String positionId = positionIds[i];
            if (y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId).isPresent()) {
                continue;
            }
            personsToPositionsList.add(y9PersonsToPositionsManager.save(personId, positionId));
        }
        return entityToModel(personsToPositionsList);
    }

    @Override
    @Transactional
    public void deletePersons(String positionId, String[] personIds) {
        for (String personId : personIds) {
            y9PersonsToPositionsManager.delete(positionId, personId);
        }
    }

    @Override
    @Transactional
    public void deletePositions(String personId, String[] positionIds) {
        for (String positionId : positionIds) {
            y9PersonsToPositionsManager.delete(positionId, personId);
        }
    }

    @Override
    public List<PersonsPositions> listByPersonId(String personId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = y9PersonsToPositionsRepository.findByPersonId(personId);
        return entityToModel(y9PersonsToPositionsList);
    }

    @Override
    public List<PersonsPositions> listByPositionId(String positionId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList =
            y9PersonsToPositionsRepository.findByPositionIdOrderByPersonOrder(positionId);
        return entityToModel(y9PersonsToPositionsList);
    }

    @Override
    public List<String> listPositionIdsByPersonId(String personId) {
        List<Y9PersonsToPositions> list =
            y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        return list.stream().map(Y9PersonsToPositions::getPositionId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<PersonsPositions> orderPersons(String positionId, String[] personIds) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {
            Optional<Y9PersonsToPositions> optionalY9PersonsToPositions =
                y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personIds[i]);
            if (optionalY9PersonsToPositions.isPresent()) {
                Y9PersonsToPositions updatedPersonsToPositions = optionalY9PersonsToPositions.get();
                Y9PersonsToPositions originalPersonsToPositions = new Y9PersonsToPositions();
                Y9BeanUtil.copyProperties(updatedPersonsToPositions, originalPersonsToPositions);
                updatedPersonsToPositions.setPersonOrder(i);

                final Y9PersonsToPositions savedPersonsToPositions =
                    y9PersonsToPositionsRepository.save(updatedPersonsToPositions);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalPersonsToPositions, savedPersonsToPositions));

                y9PersonsToPositionsList.add(savedPersonsToPositions);
            }
        }
        return entityToModel(y9PersonsToPositionsList);
    }

    @Override
    @Transactional
    public List<PersonsPositions> orderPositions(String personId, String[] positionIds) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = new ArrayList<>();

        for (int i = 0; i < positionIds.length; i++) {
            Optional<Y9PersonsToPositions> optionalY9PersonsToPositions =
                y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionIds[i], personId);
            if (optionalY9PersonsToPositions.isPresent()) {
                Y9PersonsToPositions updatedPersonsToPositions = optionalY9PersonsToPositions.get();
                Y9PersonsToPositions originalPersonsToPositions = new Y9PersonsToPositions();
                Y9BeanUtil.copyProperties(updatedPersonsToPositions, originalPersonsToPositions);
                updatedPersonsToPositions.setPositionOrder(i);

                final Y9PersonsToPositions savedPersonsToPositions =
                    y9PersonsToPositionsRepository.save(updatedPersonsToPositions);
                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalPersonsToPositions, savedPersonsToPositions));

                y9PersonsToPositionsList.add(savedPersonsToPositions);
            }
        }
        return entityToModel(y9PersonsToPositionsList);
    }

    @Override
    @Transactional
    public void saveOrUpdate(PersonsPositions personsPositions) {
        Y9PersonsToPositions y9PersonsToPositions =
            PlatformModelConvertUtil.convert(personsPositions, Y9PersonsToPositions.class);
        y9PersonsToPositionsManager.saveOrUpdate(y9PersonsToPositions);
    }

    @Override
    public List<Person> listPersonByPositionId(String positionId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList =
            y9PersonsToPositionsRepository.findByPositionIdOrderByPersonOrder(positionId);
        List<Y9Person> y9PersonList = new ArrayList<>();
        for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
            y9PersonList.add(y9PersonManager.getByIdFromCache(y9PersonsToPositions.getPersonId()));
        }
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonList);
    }

    @Override
    public String getPositionIdsByPersonId(String personId) {
        List<String> positionIdList = y9PersonsToPositionsRepository.listPositionIdsByPersonId(personId);
        return StringUtils.join(positionIdList, ",");
    }

    private List<PersonsPositions> entityToModel(List<Y9PersonsToPositions> personsToPositionsList) {
        return PlatformModelConvertUtil.convert(personsToPositionsList, PersonsPositions.class);
    }

    private PersonsPositions entityToModel(Y9PersonsToPositions y9PersonsToPositions) {
        return PlatformModelConvertUtil.convert(y9PersonsToPositions, PersonsPositions.class);
    }
}
