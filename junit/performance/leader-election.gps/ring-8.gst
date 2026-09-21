<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="ring-8">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>40 40 30 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>130 40 30 30</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>220 40 30 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>310 40 30 30</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>400 40 30 30</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>40 110 30 30</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>130 110 30 30</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>220 110 30 30</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>310 110 30 30</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>400 110 30 30</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>40 180 30 30</string>
            </attr>
        </node>
        <node id="n11">
            <attr name="layout">
                <string>130 180 30 30</string>
            </attr>
        </node>
        <node id="n12">
            <attr name="layout">
                <string>220 180 30 30</string>
            </attr>
        </node>
        <node id="n13">
            <attr name="layout">
                <string>310 180 30 30</string>
            </attr>
        </node>
        <node id="n14">
            <attr name="layout">
                <string>400 180 30 30</string>
            </attr>
        </node>
        <node id="n15">
            <attr name="layout">
                <string>40 250 30 30</string>
            </attr>
        </node>
        <node id="n16">
            <attr name="layout">
                <string>130 250 30 30</string>
            </attr>
        </node>
        <node id="n17">
            <attr name="layout">
                <string>220 250 30 30</string>
            </attr>
        </node>
        <node id="n18">
            <attr name="layout">
                <string>310 250 30 30</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>Scheduler</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>Numbers</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>int:-1</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n7" to="n7">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n7" to="n7">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n9" to="n9">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n9" to="n9">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n10" to="n10">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n10" to="n10">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n11" to="n11">
            <attr name="label">
                <string>int:2</string>
            </attr>
        </edge>
        <edge from="n3" to="n11">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n3" to="n11">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n3" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n12" to="n12">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge from="n4" to="n12">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n4" to="n12">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n4" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n4" to="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n4">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n13" to="n13">
            <attr name="label">
                <string>int:6</string>
            </attr>
        </edge>
        <edge from="n5" to="n13">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n5" to="n13">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n5" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n5" to="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n5">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n14" to="n14">
            <attr name="label">
                <string>int:3</string>
            </attr>
        </edge>
        <edge from="n6" to="n14">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n6" to="n14">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n6" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n6" to="n7">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n6">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n15" to="n15">
            <attr name="label">
                <string>int:4</string>
            </attr>
        </edge>
        <edge from="n7" to="n15">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n7" to="n15">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n7" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n7" to="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n7">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n16" to="n16">
            <attr name="label">
                <string>int:5</string>
            </attr>
        </edge>
        <edge from="n8" to="n16">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n8" to="n16">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n8" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n8" to="n9">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n8">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n17" to="n17">
            <attr name="label">
                <string>int:8</string>
            </attr>
        </edge>
        <edge from="n9" to="n17">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n9" to="n17">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n9" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n9" to="n10">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n18" to="n18">
            <attr name="label">
                <string>int:7</string>
            </attr>
        </edge>
        <edge from="n10" to="n18">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n10" to="n18">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge from="n10" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n10" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n10">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
    </graph>
</gxl>
